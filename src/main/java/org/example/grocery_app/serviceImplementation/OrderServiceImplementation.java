package org.example.grocery_app.serviceImplementation;

import lombok.Setter;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.dto.*;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartRepository;
import org.example.grocery_app.repository.InventoryRepository;
import org.example.grocery_app.repository.OrderRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.CartService;
import org.example.grocery_app.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplementation implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private  HelperMethod helperMethod;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public OrderDto createOrder(Long userId) {
        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Fetch the user's active cart
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("No active cart found for this user"));
        if(cart.getCartItems().isEmpty()){
            throw new ApiException("No cart item or any product  found in this user's cart");
        }

        // Create a new order
        Order order = new Order();
//        order.setUser(user);
        order.setOrderStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());
        order.setCart(cart);

        // Set payment details
        Payment payment = new Payment();
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentStatus("PENDING");
        payment.setPaymentMode("ONLINE");
        order.setPayment(payment);
        payment.setOrder(order);


        // Set delivery details
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setDeliveryStatus("PENDING");
        order.setDelivery(delivery);

        Map<Inventory,Integer> inventoryQuntity = new HashMap<>();
        Set<Long>inventoryIds = new HashSet<>();
        cart.getCartItems().forEach(cartItem -> {
            Inventory inventory = cartItem.getProduct().getInventory();
            inventoryQuntity.put(inventory, cartItem.getQuantity());
            inventoryIds.add(inventory.getId());
        });
        List<Inventory> allInventoryByIds = this.inventoryRepository.findAllById(inventoryIds);
        System.out.println("hello dhokhebaaz shikha");
        System.out.println(allInventoryByIds);
        allInventoryByIds.forEach(inventory -> {
            int stockQuantity = inventory.getStockQuantity();
            int quantitySold = inventoryQuntity.get(inventory);
            int stockQuantity1= stockQuantity - quantitySold;
            System.out.println(stockQuantity);
            System.out.println(quantitySold);
            System.out.println(stockQuantity1+" 0000"+"shashi kumar kushwaha");
            inventory.setStockQuantity(stockQuantity1);
            this.inventoryRepository.save(inventory);

        });
        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Mark the cart as completed
        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);



        // Create a new active cart for the user
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setStatus(CartStatus.ACTIVE);
        newCart.setCartItems(new HashSet<>()); 
        cartRepository.save(newCart);

        // Return the saved order as DTO
        OrderDto orderDto1 = modelMapper.map(savedOrder, OrderDto.class);
        PaymentDto paymentDto = this.modelMapper.map(savedOrder.getPayment(), PaymentDto.class);
        orderDto1.setPaymentDto(paymentDto);

        DeliveryDto deliveryDto = this.modelMapper.map(savedOrder.getDelivery(), DeliveryDto.class);
        deliveryDto.setDeliveryId(savedOrder.getDelivery().getId());
        orderDto1.setDeliveryDto(deliveryDto);
        Cart cart1 = savedOrder.getCart();
        Set<CartItem> cartItems = cart1.getCartItems();
        System.out.println(cartItems);
        Set<CartItemDto> cartItemDto1 = cartItems.stream().map(cartItem -> this.modelMapper.map(cartItem, CartItemDto.class)).collect(Collectors.toSet());

        CartDto cartDto = this.modelMapper.map(cart1, CartDto.class);
        System.out.println(cart.getTotalPricesOfAllProduct());
        cartDto.setCartTotalPrice(savedOrder.getCart().getTotalPricesOfAllProduct());
//        System.out.println(cartItemDto1);
        cartDto.setCartItemsDto(cartItemDto1);
        orderDto1.setCartDto(cartDto);
//        return orderDto1;
        return orderDto1;
    }

}

