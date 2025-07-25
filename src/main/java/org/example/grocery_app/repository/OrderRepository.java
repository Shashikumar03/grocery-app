package org.example.grocery_app.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderTime DESC")
    List<Order> findLatestOrdersByUser(@Param("user") User user, Pageable pageable);


    //    Optional<Order> findById(Long orderId);
    Optional<Order> findByPaymentId(Long id);

    Optional<Order> findByPayment_RozerpayId(String rozerpayPaymentId);

//    List<Order> findByAdminNotifiedFalseAndOrderTimeAfter(LocalDateTime time);
    List<Order> findByAdminNotifiedFalseAndOrderTimeAfterOrderByOrderTimeDesc(LocalDateTime time);

    List<Order> findByAdminNotifiedFalse(); // For debugging
    List<Order> findByOrderTimeBetween(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = :status WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);

    List<Order> findByOrderTimeBetweenAndOrderStatusIn(LocalDateTime start, LocalDateTime end, List<String> statuses);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'COMPLETED' AND o.orderTime BETWEEN :start AND :end")
    List<Order> findCompletedOrdersToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}
