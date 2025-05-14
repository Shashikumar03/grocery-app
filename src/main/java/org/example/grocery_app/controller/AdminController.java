package org.example.grocery_app.controller;

import org.example.grocery_app.dto.OrderDetailsToAdminDto;
import org.example.grocery_app.dto.OrderDto;
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

}
