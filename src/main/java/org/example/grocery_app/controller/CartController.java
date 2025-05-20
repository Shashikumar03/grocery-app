package org.example.grocery_app.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.CartDto;
//import org.example.grocery_app.payload.CartItemRequest;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add product to cart
    @PostMapping("/{userId}/add")

    public ResponseEntity<CartDto> addProductToCart(
            @PathVariable Long userId, @Valid @RequestBody CartItemDto cartItem) {


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
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> viewCart(@PathVariable Long userId) {
        CartDto cartDto = cartService.viewUserCart(userId);

        return ResponseEntity.ok(cartDto);
    }
//
//    // Clear cart
//    @DeleteMapping("/{userId}/clear")
//    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
//        cartService.clearCart(userId);
//        return ResponseEntity.noContent().build();
//    }

    @PutMapping("/remove/{userId}/{cardItemId}")
    public ResponseEntity<CartDto> removeProductFromCart(@PathVariable Long userId,@PathVariable Long cardItemId) {
        CartDto cartDto = this.cartService.removeProductFromCart(userId, cardItemId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }
    @PutMapping("/{cartId}/discount")
    public ResponseEntity<String> updateDiscount(@PathVariable Long cartId, @RequestParam double discount) {
        cartService.updateCartDiscount(cartId, discount);
        return ResponseEntity.ok("Discount updated successfully");
    }

}
