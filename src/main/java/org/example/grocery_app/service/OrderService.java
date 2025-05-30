package org.example.grocery_app.service;

import org.example.grocery_app.dto.OrderDetailsToAdminDto;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(Long userId, Long deliveryAddressId, String paymentMode);
    List<OrderDto> getOrderByUser(Long userId);

    OrderDto getOrderById(Long orderId);

    OrderDto cancelOrder(Long orderId, String reason);

    OrderDto confirmGoodsReturnedAndRefund(Long razorpayPaymentId);

    List<OrderDetailsToAdminDto> getOrdersPlacedWithinLastMinute();

    public void updateStatus(Long orderId, String status);

    List<OrderDetailsToAdminDto> getTodayCancelledAndCompletedOrdersGroupedByUser();

}
