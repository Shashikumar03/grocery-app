package org.example.grocery_app.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.constant.PaymentMode;
import org.example.grocery_app.dto.OrderDetailsToAdminDto;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/place-order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}/{delivery-addressId}")
    public ResponseEntity<OrderDto> getPlaceOrder(
            @PathVariable Long userId,
            @PathVariable("delivery-addressId") Long deliveryAddressId,
            @RequestParam("paymentMode") String paymentModeStr) {


        OrderDto orderDto = this.orderService.createOrder(userId, deliveryAddressId, paymentModeStr);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @GetMapping("history/{userId}")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable Long userId) {
        List<OrderDto> orderByUser = this.orderService.getOrderByUser(userId);
        log.info("oder of users :{}", orderByUser);
        return  new ResponseEntity<>(orderByUser, HttpStatus.OK);
    }

    @GetMapping("update/{orderId}")
    public  ResponseEntity<OrderDto> updateOrder(@PathVariable Long orderId){
        OrderDto orderById = this.orderService.getOrderById(orderId);
        return  new ResponseEntity<>(orderById, HttpStatus.OK);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId,@RequestParam("reason") String reason ) {
        OrderDto cancelledOrder = orderService.cancelOrder(orderId, reason);
        return new ResponseEntity<>(cancelledOrder, HttpStatus.OK);
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok("Order status updated to " + status);
    }

    @GetMapping("/today-cancel-and_completed-order")
    public ResponseEntity<List<OrderDetailsToAdminDto>> getTodayCancelledAndCompletedOrdersGroupedByUser(){
        List<OrderDetailsToAdminDto> todayCancelledAndCompletedOrdersGroupedByUser = this.orderService.getTodayCancelledAndCompletedOrdersGroupedByUser();
        return  new ResponseEntity<>(todayCancelledAndCompletedOrdersGroupedByUser,HttpStatus.OK);
    }



}
