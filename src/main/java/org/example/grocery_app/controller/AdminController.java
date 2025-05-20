package org.example.grocery_app.controller;

import org.example.grocery_app.dto.OrderDetailsToAdminDto;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.security.JwtHelper;
import org.example.grocery_app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private  OrderService orderService;
    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/{paymentId}/confirm-return")
    public ResponseEntity<OrderDto> confirmGoodsReturnedAndInitiateRefund(@PathVariable Long paymentId) {
        OrderDto orderDto = orderService.confirmGoodsReturnedAndRefund(paymentId);
        return ResponseEntity.ok(orderDto);
    }


    @GetMapping("/recent-orders")
    public ResponseEntity<List<OrderDetailsToAdminDto>> getRecentOrders() {
        List<OrderDetailsToAdminDto> ordersPlacedWithinLastMinute = orderService.getOrdersPlacedWithinLastMinute();
        return ResponseEntity.ok(ordersPlacedWithinLastMinute);
    }
    @GetMapping("/istokenexpire")
    public boolean isTokenExpire(@RequestHeader("Authorization") String authorizationHeader) {
        // The token usually comes as "Bearer <token>", so you need to extract the token part
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            // handle missing or malformed token as needed
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        return jwtHelper.isTokenExpired(token);
    }

}
