package org.example.grocery_app.service;

import org.example.grocery_app.dto.CartDto;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.entities.CartItem;

public interface CartService {

    CartDto addProductToCart(Long userId, CartItemDto cartItemDto);
    CartDto removeProductFromCart(Long userId, Long productId);
    CartDto viewUserCart(Long userId);
    public void updateCartDiscount(Long cartId, double newDiscountAmount);
    
}
