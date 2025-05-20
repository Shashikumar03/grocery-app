package org.example.grocery_app.repository;

import jakarta.transaction.Transactional;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ Correct import

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.discountAmount = :discount WHERE c.id = :cartId")
    void updateDiscount(@Param("cartId") Long cartId, @Param("discount") double discount);
}
