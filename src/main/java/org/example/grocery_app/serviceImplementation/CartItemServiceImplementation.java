package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.constant.Action;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.entities.CartItem;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartItemRepository;
import org.example.grocery_app.service.CartItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemServiceImplementation implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private HelperMethod helperMethod;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public CartItemDto getCartItemById(Long cartItemId) {
        CartItem cartItem = this.cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("cartItem", "cartItemId", cartItemId));
       return  this.helperMethod.changeCartItemIntoCartItemDto(cartItem);
    }

    @Override
    public CartItemDto updateCartItem(Long cartItemId, String action) {
        CartItem cartItem = this.cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("cartItem", "cartItemId", cartItemId));
        // Check the action against the Action enum
        if (Action.ADD.name().equalsIgnoreCase(action)) {
            // Perform the logic for adding
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else if (Action.DEC.name().equalsIgnoreCase(action)) {
            // Perform the logic for removing
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            if (cartItem.getQuantity() <= 0) {
                this.cartItemRepository.delete(cartItem);
                throw  new  ApiException("this product is removed");
            }
        } else {
            throw new ApiException("Invalid action: " + action);
        }

        // Save the updated cartItem
        CartItem updatedCartItem = this.cartItemRepository.save(cartItem);

        // Convert the updated cart item to CartItemDto and return
        return this.helperMethod.changeCartItemIntoCartItemDto(updatedCartItem);

    }

    @Override
    public CartItemDto getCartItemByProductId(Long productId) {
        CartItem cartItem = this.cartItemRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("cartItem not found with", "productId", productId));
        return this.modelMapper.map(cartItem, CartItemDto.class);

    }
}
