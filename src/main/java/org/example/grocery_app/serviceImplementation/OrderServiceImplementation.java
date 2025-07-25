package org.example.grocery_app.serviceImplementation;
//package org.example.grocery_app.serviceImplementation;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kotlin.io.CloseableKt;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.constant.PaymentMode;
import org.example.grocery_app.dto.*;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.*;
//import org.example.grocery_app.security.SecurityUtils;
import org.example.grocery_app.service.EmailSenderService;
import org.example.grocery_app.service.OrderService;
import org.example.grocery_app.service.PaymentGatewayService;
import org.example.grocery_app.util.CodeGenerator;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private PaymentRepository paymentRepository;

    @Autowired
    private HttpServletRequest request;
//
//    @Autowired
//    private  PaymentGatewayService paymentGatewayService;

//    @Autowired
//    private SecurityUtils securityUtils;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    ProductRepository productRepository;

    @PostConstruct
    public void initRazorpayClient() {
        try {
            this.razorpayClient = new RazorpayClient("rzp_live_UDn0rqtiftjbPd", "78IeWxDMwC8M6C8YUffofuKh");
        } catch (RazorpayException e) {
            log.error("Failed to initialize Razorpay Client", e);
            throw new RuntimeException("Razorpay client initialization failed.");
        }
    }

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, Long deliveryAddressId, String paymentModeStr) {
//        this.securityUtils.validateUserAccess(userId, request);
        User user = getUserById(userId);
        log.info("User found successfully :{}", user);
        PaymentMode paymentMode;
        try {
            paymentMode = PaymentMode.valueOf(paymentModeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid payment mode. Allowed values: ONLINE, CASH_ON_DELIVERY");
        }

        Cart cart = getActiveCartForUser(user);
        DeliveryAddress deliveryAddress = this.deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId).orElseThrow(() -> new ResourceNotFoundException("Address", "Delivery Address Id", deliveryAddressId));

        validateCartItems(cart);
        if (cart.getTotalPricesOfAllProduct() < 100) {
            throw new ApiException("कृपया कम से कम ₹100 का ऑर्डर करें");
        }
        log.info("Card validate successfully}");
//        double discountAmount = cart.getDiscountAmount();
//        handle the inventory also available or not
        Order order = initializeOrder(user, cart);


//        Setting address of in the order
//        order.setDiscountOnOrder(discountAmount);
        order.setAddress(deliveryAddress.getAddress());
        order.setLandmark(deliveryAddress.getLandmark());
        order.setMobile(deliveryAddress.getMobile());
        order.setCity(deliveryAddress.getCity());
        order.setPin(deliveryAddress.getPin());
        order.setState(deliveryAddress.getState());
        order.setPaymentMode(paymentMode.name());

        log.info("Order Created Successfully :{}", order);

        Payment payment;
        if (paymentMode == PaymentMode.ONLINE) {

            payment = createRazorpayOrder(cart);  // Razorpay or any other gateway
            payment.setPaymentMode(PaymentMode.ONLINE.name());
            throw new ApiException("online payment not accepting now");
//            payment.setPaymentStatus(PaymentStatus.PENDING); //
        } else if (paymentMode == PaymentMode.CASH_ON_DELIVERY) {
            payment = new Payment();
            payment.setRozerpayId(CodeGenerator.generateCashCode());
            payment.setPaymentAmount(cart.getTotalPricesOfAllProduct());
            payment.setPaymentMode(PaymentMode.CASH_ON_DELIVERY.name());
            payment.setPaymentTime(LocalDateTime.now());
            payment.setPaymentStatus("created");
            payment.setPaymentTime(null);
        } else {
            throw new ApiException("Unsupported payment mode: " + paymentMode);
        }
        order.setPayment(payment);
        payment.setOrder(order);

        Delivery deliveryAgent = createDelivery(order);
        order.setDelivery(deliveryAgent);

        updateInventoryStock(cart);
        log.info("Inventory update successfully");

        Order savedOrder = orderRepository.save(order);
        log.info("order created successfully :{}", savedOrder);

        completeUserCart(cart); //clearUserCart
        createNewActiveCart(user);

        // Build HTML email body
        String htmlBody = """
                    <h2 style="color: #2e6c80;">🛒 Order Confirmation - Bazzario</h2>
                    <p>Hello <strong>%s</strong>,</p>
                    <p>Thank you for your order! Here are the details:</p>
                
                    <table style="border: 1px solid #ccc; border-collapse: collapse;">
                        <tr><td><strong>Order ID:</strong></td><td>%s</td></tr>
                          <tr><td><strong>User Mob:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Payment Mode:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Total Amount:</strong></td><td>₹%.2f</td></tr>
                        <tr><td><strong>Delivery City:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Address:</strong></td><td>%s</td></tr>
                    </table>
                
                    <p style="margin-top: 16px;">📦 Your order will be delivered soon. We’ll notify you when it’s out for delivery.</p>
                
                    <p>Thanks for shopping with Bazzario!<br/>— Team Bazzario</p>
                """.formatted(
                user.getName(),
                savedOrder.getId(),
                user.getPhoneNumber(),
                savedOrder.getPaymentMode(),
                cart.getTotalPricesOfAllProduct(),
                deliveryAddress.getCity(),
                deliveryAddress.getAddress()
        );
        // Send email to the customer or admin
        String[] recipients = {
                "shashikumarkushwaha1@gmail.com",
                "ry4715885@gmail.com",
//                "shashikumarkushwaha1@gmail.com",
//                "naveensinghicici@gmail.com",
//                user.getEmail(),
        };
//        String a="ry4715885@gmail.com";
        emailSenderService.sendSimpleEmail(
                recipients,
                htmlBody,
                "🧾 Order Confirmation - Bazzario"
        );


//        this.emailSenderService.sendSimpleEmail("shashikumarkushwaha3@gmail.com","Order placed by someone please check","Bazzario Order status");

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
        notes.put("notes_key_1", "Tea, Earl Grey, Hot");
        notes.put("notes_key_1", "Tea, Earl Grey, Hot");
        orderRequest.put("notes", notes);

        try {
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            payment.setRozerpayId(razorpayOrder.get("id"));
            payment.setPaymentStatus(razorpayOrder.get("status"));
            log.info("razorpay  order details");
            log.info("razorpay order :{}", razorpayOrder);

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: ", e);
            throw new ApiException("Payment order creation failed."+e.getMessage());
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
        log.info("all inventories that has to be update :{}", inventories);

        inventories.forEach(inventory -> {
            int updatedQuantity = inventory.getStockQuantity() - inventoryQuantityMap.get(inventory);
            if (updatedQuantity < 0) {
                String name = inventory.getProduct().getName();
                throw new ApiException(name + ":is out of stock, Available qty : " + inventory.getStockQuantity());

            }
            if (updatedQuantity == 0) {
                inventory.getProduct().setAvailable(false);
//                handle this
                this.productRepository.save(inventory.getProduct());
                log.info("Product marked as unavailable due to zero stock: {}", inventory.getProduct().getName());


            }
            inventory.setStockQuantity(updatedQuantity);
            Inventory save = inventoryRepository.save(inventory);
            log.info("Inventory after updating  the items :{}", save);
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
        return orderOfUser.stream().map((order) -> {
            OrderDto orderDto = this.modelMapper.map(order, OrderDto.class);
            Payment payment = order.getPayment();
            if (payment != null) {
                PaymentDto paymentDto = this.modelMapper.map(payment, PaymentDto.class);
                orderDto.setPaymentDto(paymentDto);
            }
            Delivery delivery = order.getDelivery();
            if (delivery != null) {
                DeliveryDto deliveryDto = this.modelMapper.map(delivery, DeliveryDto.class);
                orderDto.setDeliveryDto(deliveryDto);
            } else {
                throw new ApiException("Delivery address cannot be null");
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

    @Override
    public OrderDto getOrderById(Long orderId) {
        Order order = this.orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("order", "orderId", orderId));
        log.info("order by order id : {}", order);
        return null;
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId, String reason) {
        log.info("Attempting to cancel order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for ID: {}", orderId);
                    return new ResourceNotFoundException("Order", "orderId", orderId);
                });

        if ("CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
            log.warn("Order with ID: {} is already cancelled.", orderId);
            throw new ApiException("Order is already cancelled.");
        }

        order.setOrderStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        log.info("Order ID: {} marked as CANCELLED at {}", orderId, order.getCancelledAt());

        // Inventory restoration
        Cart cart = order.getCart();
        if (cart != null && cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                Inventory inventory = item.getProduct().getInventory();
                int newStock = inventory.getStockQuantity() + item.getQuantity();
                if (newStock > 0 && !item.getProduct().isAvailable()) {
                    item.getProduct().setAvailable(true);
                }
                inventory.setStockQuantity(newStock);
                inventoryRepository.save(inventory);
                log.info("Restored {} units to inventory for product ID: {}", item.getQuantity(), item.getProduct().getId());
            }
        } else {
            log.error("Cart is missing or empty for order ID: {}", orderId);
            throw new ApiException("Cart issue, cannot cancel the order");
        }

        // Set flag: waiting for goods return
        order.setGoodsReturned(false);
        log.info("Set goodsReturned = false for order ID: {}", orderId);

        // Handle payment details and store refund context
        Payment payment = order.getPayment();
        if (payment != null && Objects.equals(payment.getPaymentMode(), PaymentMode.ONLINE.name()) &&
                ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus()) ||
                        "CREATED".equalsIgnoreCase(payment.getPaymentStatus()))) {
//            ydi online payment nhi huya hai to refund kaise de skte hai
            if ("CREATED".equalsIgnoreCase(payment.getPaymentStatus())) {
                log.warn("Payment not completed for payment ID: {}. Refund not allowed.", payment.getRozerpayId());
                throw new ApiException("Payment has not been completed for payment ID: " + payment.getRozerpayId() + ", so unable to perform the refund.");
            }

            double paymentAmount = payment.getPaymentAmount();
            double refundAmount = paymentAmount;

            payment.setRefundStatus("WAITING_FOR_RETURN");
            payment.setRefundAmount(refundAmount);
            payment.setRefundInitiated(false);
            payment.setPaymentNotes("Order cancelled. Within 2-5 working days.");

            this.paymentGatewayService.initiatePartialRefund(payment.getPaymentId(), refundAmount);
            paymentRepository.save(payment);

            log.info("Refund of ₹{} scheduled for payment ID: {} (after 10% deduction)", refundAmount, payment.getRozerpayId());
        } else if (payment != null && Objects.equals(payment.getPaymentMode(), PaymentMode.CASH_ON_DELIVERY.name()) &&
                ("PENDING".equalsIgnoreCase(payment.getPaymentStatus()) ||
                        "CREATED".equalsIgnoreCase(payment.getPaymentStatus()))) {
            if (payment.getPaymentStatus() == "COMPLETED") {
                throw new ApiException("Item Delivered, Please refresh the Page");
            }
            double paymentAmount = payment.getPaymentAmount();
            double refundAmount = paymentAmount;
            payment.setRefundStatus("WAITING_FOR_RETURN");
            payment.setRefundAmount(refundAmount);
            payment.setRefundInitiated(false);
            payment.setPaymentNotes("Order cancelled. Refund will be process by our finance team");
            paymentRepository.save(payment);
        }

        orderRepository.save(order);
        log.info("Order cancellation complete for ID: {}", orderId);

        OrderDto orderDto = convertToOrderDto(order);
        orderDto.setNote("Order cancelled. Refund will be processed once goods are returned.");

        return orderDto;
    }

    @Override
    public OrderDto confirmGoodsReturnedAndRefund(Long orderId) {
        log.info("Admin triggered refund for order ID: {}", orderId);

        Order order = orderRepository.findByPaymentId(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for ID: {}", orderId);
                    return new ResourceNotFoundException("Order", "orderId" + orderId, 0);
                });

        if (!"CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
            log.warn("Order ID: {} is not in CANCELLED state. Cannot proceed.", orderId);
            throw new ApiException("Only cancelled orders can be refunded.");
        }

        if (Boolean.TRUE.equals(order.isGoodsReturned())) {
            log.warn("Goods already marked as returned for order ID: {}", orderId);
            throw new ApiException("Goods already returned. Refund might have been processed.");
        }

        order.setGoodsReturned(true);
        log.info("Marked goods as returned for order ID: {}", orderId);

        Payment payment = order.getPayment();
        if (payment == null) {
            log.error("No payment found for order ID: {}", orderId);
            throw new ApiException("No payment associated with this order.");
        }

        if (!"WAITING_FOR_RETURN".equalsIgnoreCase(payment.getRefundStatus())) {
            log.warn("Refund not expected for payment ID: {}. Status: {}", payment.getRozerpayId(), payment.getRefundStatus());
            throw new ApiException("Refund is not pending or already processed.");
        }

        if (payment.isRefundInitiated()) {
            log.warn("Refund already initiated for payment ID: {}", payment.getRozerpayId());
            throw new ApiException("Refund already initiated.");
        }
        if (Objects.equals(payment.getPaymentMode(), PaymentMode.ONLINE.name())) {
            String refundResponse = paymentGatewayService.initiatePartialRefund(payment.getPaymentId(), payment.getRefundAmount());
            log.info("Refund initiated: payment ID = {}, amount = {}, response = {}", payment.getRozerpayId(), payment.getRefundAmount(), refundResponse);
            payment.setPaymentNotes("Refund initiated by admin after goods return. It will take 2-5 working days to complete");

        } else {
            double refundAmount = payment.getRefundAmount() * 0.90;
//            int refundAmountInPaise = (int) (refundAmount * 100);
            payment.setRefundAmount(refundAmount);
            payment.setPaymentNotes("COD refund pending. To be handled manually via UPI/bank transfer by finance team.");

        }

        payment.setRefundInitiated(true);
        payment.setRefundStatus("REFUND_INITIATED");

        paymentRepository.save(payment);
        orderRepository.save(order);

        OrderDto orderDto = convertToOrderDto(order);
        orderDto.setNote("Refund initiated for order ID " + orderId + " after goods return.");

        return orderDto;
    }

    @Override
    public List<OrderDetailsToAdminDto> getOrdersPlacedWithinLastMinute() {
//        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(100);
//        log.info("time :{}", oneMinuteAgo);
//        log.info("Fetching orders placed after: {}", oneMinuteAgo);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // today at 00:00
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        log.info("time :{}", startOfDay);
        log.info("end date :{}", endOfDay);
        // Fetch all unnotified orders placed in redisthe last minute
        List<Order> recentOrders = orderRepository.findByOrderTimeBetween(startOfDay, endOfDay);
        log.info("recent orders are :{}", recentOrders);
        // Mark as notified
//        for (Order order : recentOrders) {
//            order.setAdminNotified(true);
//        }
//        orderRepository.saveAll(recentOrders);

        // Group by user
        Map<User, List<Order>> groupedByUser = recentOrders.stream()
                .collect(Collectors.groupingBy(Order::getUser));

        List<OrderDetailsToAdminDto> result = new ArrayList<>();

        for (Map.Entry<User, List<Order>> entry : groupedByUser.entrySet()) {
            User user = entry.getKey();
            List<Order> orders = entry.getValue();

            UserDto userDto = modelMapper.map(user, UserDto.class);
            List<OrderDto> orderDtos = orders.stream()
                    .filter(order -> "PENDING".equalsIgnoreCase(order.getOrderStatus()))
                    .filter(order -> !"created".equalsIgnoreCase(order.getPayment().getPaymentStatus()) || "CASH_ON_DELIVERY".equalsIgnoreCase(order.getPayment().getPaymentMode()) && "created".equalsIgnoreCase(order.getPayment().getPaymentStatus()))
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

            if (!orderDtos.isEmpty()) {
                result.add(new OrderDetailsToAdminDto(userDto, orderDtos));
            }
        }

        return result;
    }


    @Override
    public void updateStatus(Long orderId, String status) {
        // Optional: Validate status (e.g., check allowed values)
        orderRepository.updateOrderStatus(orderId, status);
    }

    @Override
    public List<OrderDetailsToAdminDto> getTodayCancelledAndCompletedOrdersGroupedByUser() {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<String> validStatuses = Arrays.asList("CANCELLED", "COMPLETED");

        List<Order> todayOrders = orderRepository.findByOrderTimeBetweenAndOrderStatusIn(startOfDay, endOfDay, validStatuses);
        // Group by user
        Map<User, List<Order>> ordersByUser = todayOrders.stream()
                .collect(Collectors.groupingBy(Order::getUser));

        List<OrderDetailsToAdminDto> response = new ArrayList<>();

        for (Map.Entry<User, List<Order>> entry : ordersByUser.entrySet()) {
            User user = entry.getKey();
            List<Order> orders = entry.getValue();

            UserDto userDto = modelMapper.map(user, UserDto.class);

            List<OrderDto> orderDtos = orders.stream()
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

            response.add(new OrderDetailsToAdminDto(userDto, orderDtos));
        }

        return response;
    }


}

