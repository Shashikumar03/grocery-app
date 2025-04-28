package org.example.grocery_app.serviceImplementation;
//package org.example.grocery_app.serviceImplementation;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kotlin.io.CloseableKt;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.dto.*;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.*;
import org.example.grocery_app.security.SecurityUtils;
import org.example.grocery_app.service.OrderService;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImplementation implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.secret.key}")
    private String razorpaySecretKey;

    private RazorpayClient razorpayClient;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SecurityUtils securityUtils;

    @PostConstruct
    public void initRazorpayClient() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKey, razorpaySecretKey);
        } catch (RazorpayException e) {
            log.error("Failed to initialize Razorpay Client", e);
            throw new RuntimeException("Razorpay client initialization failed.");
        }
    }

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, Long deliveryAddressId) {

        this.securityUtils.validateUserAccess(userId, request);

        User user = getUserById(userId);
        log.info("User found successfully :{}",user);
        Cart cart = getActiveCartForUser(user);
        DeliveryAddress deliveryAddress = this.deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId).orElseThrow(() -> new ResourceNotFoundException("Address", "Delivery Address Id", deliveryAddressId));


        validateCartItems(cart);
        log.info("Card validate successfully}");

        Order order = initializeOrder(user, cart);

//        Setting address of in the order
        order.setAddress(deliveryAddress.getAddress());
        order.setLandmark(deliveryAddress.getLandmark());
        order.setMobile(deliveryAddress.getMobile());
        order.setCity(deliveryAddress.getCity());
        order.setPin(deliveryAddress.getPin());
        order.setState(deliveryAddress.getState());


        log.info("Order Created Successfully :{}",order);
        Payment payment = createRazorpayOrder(cart);
        log.info("payment created Successfully {}",payment);


        order.setPayment(payment);
        payment.setOrder(order);

        Delivery deliveryAgent = createDelivery(order);
        order.setDelivery(deliveryAgent);

        updateInventoryStock(cart);
        log.info("Inventory update successfully");

        Order savedOrder = orderRepository.save(order);
        log.info("order created successfully :{}",savedOrder);

        completeUserCart(cart); //clearUserCart
        createNewActiveCart(user);

        return convertToOrderDto(savedOrder);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
    }

    private Cart getActiveCartForUser(User user) {
        return cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("No active cart found for this user"));
    }

    private void validateCartItems(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new ApiException("No items found in the user's cart");
        }
    }

    private Order initializeOrder(User user, Cart cart) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());
        order.setCart(cart);
        return order;
    }

    private Payment createRazorpayOrder(Cart cart) {
        Payment payment = new Payment();
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMode("ONLINE");
        payment.setPaymentAmount(cart.getTotalPricesOfAllProduct());

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", cart.getTotalPricesOfAllProduct() * 100); // Convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt#1");
        JSONObject notes = new JSONObject();
        notes.put("notes_key_1","Tea, Earl Grey, Hot");
        notes.put("notes_key_1","Tea, Earl Grey, Hot");
        orderRequest.put("notes",notes);

        try {
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            payment.setRozerpayId(razorpayOrder.get("id"));
            payment.setPaymentStatus(razorpayOrder.get("status"));
            log.info("razorpay  order details");
            log.info("razorpay order :{}",razorpayOrder);

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: ", e);
            throw new ApiException("Payment order creation failed. Please try again later.");
        }

        return payment;
    }

    private Delivery createDelivery(Order order) {
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setDeliveryStatus("PENDING");
        return delivery;
    }

    private void updateInventoryStock(Cart cart) {
        Map<Inventory, Integer> inventoryQuantityMap = new HashMap<>();
        Set<Long> inventoryIds = new HashSet<>();

        cart.getCartItems().forEach(cartItem -> {
            Inventory inventory = cartItem.getProduct().getInventory();
            inventoryQuantityMap.put(inventory, cartItem.getQuantity());
            inventoryIds.add(inventory.getId());
        });

        List<Inventory> inventories = inventoryRepository.findAllById(inventoryIds);
        log.info("all inventries that has to be update :{}",inventories);

        inventories.forEach(inventory -> {
            int updatedQuantity = inventory.getStockQuantity() - inventoryQuantityMap.get(inventory);
            inventory.setStockQuantity(updatedQuantity);
            Inventory save = inventoryRepository.save(inventory);
            log.info("Inventory after updating  the items :{}",save);
        });
    }

    private void completeUserCart(Cart cart) {
        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);
    }

    private void createNewActiveCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setStatus(CartStatus.ACTIVE);
        newCart.setCartItems(new HashSet<>());
        cartRepository.save(newCart);
    }

    private OrderDto convertToOrderDto(Order savedOrder) {
        OrderDto orderDto = modelMapper.map(savedOrder, OrderDto.class);

        PaymentDto paymentDto = modelMapper.map(savedOrder.getPayment(), PaymentDto.class);
        orderDto.setPaymentDto(paymentDto);

        DeliveryDto deliveryDto = modelMapper.map(savedOrder.getDelivery(), DeliveryDto.class);
        orderDto.setDeliveryDto(deliveryDto);

        CartDto cartDto = convertCartToCartDto(savedOrder.getCart());
        orderDto.setCartDto(cartDto);

        return orderDto;
    }

    private CartDto convertCartToCartDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart, CartDto.class);

        Set<CartItemDto> cartItemDtos = cart.getCartItems()
                .stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDto.class))
                .collect(Collectors.toSet());

        cartDto.setCartItemsDto(cartItemDtos);
        cartDto.setCartTotalPrice(cart.getTotalPricesOfAllProduct());

        return cartDto;
    }



//@Override
//public List<OrderDto> getOrderByUser(Long userId) {
//    return List.of();
//}

    @Override
    public List<OrderDto> getOrderByUser(Long userId) {

        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        List<Order> orderOfUser = this.orderRepository.findByUser(user);
        log.info("Order of the user : {}", orderOfUser);
       return  orderOfUser.stream().map((order)->{
           OrderDto orderDto = this.modelMapper.map(order, OrderDto.class);
           Payment payment = order.getPayment();
           if(payment!=null){
               PaymentDto paymentDto = this.modelMapper.map(payment, PaymentDto.class);
               orderDto.setPaymentDto(paymentDto);
           }
           Delivery delivery = order.getDelivery();
           if (delivery != null) {
               DeliveryDto deliveryDto = this.modelMapper.map(delivery, DeliveryDto.class);
               orderDto.setDeliveryDto(deliveryDto);
           }else {
               throw  new ApiException("Delivery address cannot be null");
           }

           Cart cart = order.getCart();
//           if (cart != null) {
//               CartDto cartDto = this.modelMapper.map(cart, CartDto.class);
//               orderDto.setCartDto(cartDto);
//           }
           if (cart != null && cart.getCartItems() != null) {
               Set<CartItemDto> cartItemDto = cart.getCartItems().stream()
                       .map(cartItem -> this.modelMapper.map(cartItem, CartItemDto.class))
                       .collect(Collectors.toSet());

               orderDto.setCartItemDto(cartItemDto);
           }

           return orderDto;
       }).collect(Collectors.toList());

    }

}

