package org.example.grocery_app.service;

import org.example.grocery_app.dto.OrderDto;

public interface OrderService {

    OrderDto createOrder(Long userId);

}
