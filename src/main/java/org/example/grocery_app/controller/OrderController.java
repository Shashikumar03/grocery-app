package org.example.grocery_app.controller;

import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/place-order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderDto> getPlaceOrder(@PathVariable Long userId) {
        OrderDto order = this.orderService.createOrder(userId);
        return ResponseEntity.ok(order);
    }

}
