package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.CartItem;
import org.example.grocery_app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findByProductId(Long productId);
}
