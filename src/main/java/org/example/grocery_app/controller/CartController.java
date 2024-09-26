package org.example.grocery_app.controller;

import org.example.grocery_app.dto.CartDto;
//import org.example.grocery_app.payload.CartItemRequest;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add product to cart
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDto> addProductToCart(
            @PathVariable Long userId,
            @RequestBody CartItemDto cartItem) {

        CartDto updatedCart = cartService.addProductToCart(userId, cartItem);
        return ResponseEntity.ok(updatedCart);
    }

    // Remove product from cart
//    @DeleteMapping("/{userId}/remove/{productId}")
//    public ResponseEntity<CartDto> removeProductFromCart(
//            @PathVariable Long userId,
//            @PathVariable Long productId) {
//
//        CartDto updatedCart = cartService.removeProductFromCart(userId, productId);
//        return ResponseEntity.ok(updatedCart);
//    }
//
//    // View user's cart
//    @GetMapping("/{userId}")
//    public ResponseEntity<CartDto> viewCart(@PathVariable Long userId) {
//        CartDto cartDto = cartService.viewCart(userId);
//        return ResponseEntity.ok(cartDto);
//    }
//
//    // Clear cart
//    @DeleteMapping("/{userId}/clear")
//    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
//        cartService.clearCart(userId);
//        return ResponseEntity.noContent().build();
//    }
}
