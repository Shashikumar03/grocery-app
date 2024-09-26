package org.example.grocery_app.repository;

import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

//    Optional<Cart> findByCartId(Long cartId);
    Optional<Cart> findByUser(User user);
//    Optional

    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

}
