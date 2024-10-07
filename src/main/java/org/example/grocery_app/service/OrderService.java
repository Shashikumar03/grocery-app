package org.example.grocery_app.service;

import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;

public interface OrderService {

    OrderDto createOrder(Long userId);

}
