package org.example.grocery_app.serviceImplementation;

import lombok.Setter;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.dto.CartDto;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartRepository;
import org.example.grocery_app.repository.OrderRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.CartService;
import org.example.grocery_app.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

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
    private ModelMapper modelMapper;


    @Override
    public OrderDto createOrder(Long userId) {
        // Fetch the user and their cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Cart cart = this.cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow(() -> new ApiException("no cart found for this user " + userId));

        // Create a new order
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

//        setting payment details
        Payment payment = new Payment();
//        payment.setOrder(order);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentStatus("PENDING");
        payment.setPaymentMode("ONLINE");

        order.setPayment(payment);

//        setting Delivery
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setDeliveryStatus("PENDING");
//        delivery.setDeliveryAgent();

        order.setDelivery(delivery);

        order.setCart(cart);

        Order saveOrder = this.orderRepository.save(order);
        System.out.println(saveOrder);
        return this.modelMapper.map(saveOrder, OrderDto.class);
//        return this.helperMethod.changeOrderIntoOrderDto(saveOrder);

    }
}
