package org.example.grocery_app.service;

import org.example.grocery_app.dto.CartItemDto;

public interface CartItemService {

    CartItemDto getCartItemById(Long cartItemId);

    CartItemDto updateCartItem(Long cartItemId, String action );
    CartItemDto getCartItemByProductId(Long productId);

}
