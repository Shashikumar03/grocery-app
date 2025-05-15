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
        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("cartItem", "cartItemId", cartItemId));

        if (Action.ADD.name().equalsIgnoreCase(action)) {
            cartItem.incrementQuantity();
        } else if (Action.DEC.name().equalsIgnoreCase(action)) {
            try {
                cartItem.decrementQuantity();
            } catch (IllegalStateException e) {
                this.cartItemRepository.delete(cartItem);
                throw new ApiException("Quantity cannot be zero");
            }
        } else {
            throw new ApiException("Invalid action: " + action);
        }

        CartItem updatedCartItem = this.cartItemRepository.save(cartItem);
        return this.helperMethod.changeCartItemIntoCartItemDto(updatedCartItem);
    }


    @Override
    public CartItemDto getCartItemByProductId(Long productId) {
        CartItem cartItem = this.cartItemRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("cartItem not found with", "productId", productId));
        return this.modelMapper.map(cartItem, CartItemDto.class);

    }
}
