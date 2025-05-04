package org.example.grocery_app.service;

import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(Long userId, Long deliveryAddressId);
    List<OrderDto> getOrderByUser(Long userId);

    OrderDto getOrderById(Long orderId);
}
