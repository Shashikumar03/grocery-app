package org.example.grocery_app.controller;

import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cartItem")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable Long cartItemId) {
        CartItemDto cartItemById = this.cartItemService.getCartItemById(cartItemId);
        return new ResponseEntity<>(cartItemById, HttpStatus.OK);
    }
    @PutMapping("/{cartItemId}/{action}")
    public ResponseEntity<CartItemDto> updateCartItem(@PathVariable Long cartItemId, @PathVariable String action) {
        CartItemDto cartItemById = this.cartItemService.updateCartItem(cartItemId, action);
        return  new ResponseEntity<>(cartItemById, HttpStatus.OK);
    }

//    @GetMapping("product/{productId}")
//    public  ResponseEntity<CartItemDto> getCardItemByProductId(@PathVariable Long productId){
//        CartItemDto cartItemByProductId = this.cartItemService.getCartItemByProductId(productId);
//        return  new ResponseEntity<>(cartItemByProductId, HttpStatus.OK);
//    }
}
