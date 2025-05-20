package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.constant.Action;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.entities.CartItem;
import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartItemRepository;
import org.example.grocery_app.repository.InventoryRepository;
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
    @Autowired
    private InventoryRepository inventoryRepository;
    @Override
    public CartItemDto getCartItemById(Long cartItemId) {
        CartItem cartItem = this.cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("cartItem", "cartItemId", cartItemId));
       return  this.helperMethod.changeCartItemIntoCartItemDto(cartItem);
    }

    @Override
    public CartItemDto updateCartItem(Long cartItemId, String action) {
        // Find the cart item or throw if not found
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "cartItemId", cartItemId));

        Inventory inventory = cartItem.getProduct().getInventory();

        if (Action.ADD.name().equalsIgnoreCase(action)) {
            // Check if there's enough stock before adding
            if (cartItem.getQuantity() + 1 > inventory.getStockQuantity()) {
                throw new ApiException("Item out of stock. Only Available: " + inventory.getStockQuantity());
            }
            cartItem.incrementQuantity();
        } else if (Action.DEC.name().equalsIgnoreCase(action)) {
            try {
                cartItem.decrementQuantity();
            } catch (IllegalStateException e) {
                cartItemRepository.delete(cartItem);
                throw new ApiException("Quantity cannot be zero. Item removed from cart.");
            }
        } else {
            throw new ApiException("Invalid action: " + action);
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return helperMethod.changeCartItemIntoCartItemDto(updatedCartItem);
    }


    @Override
    public CartItemDto getCartItemByProductId(Long productId) {
        CartItem cartItem = this.cartItemRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("cartItem not found with", "productId", productId));
        return this.modelMapper.map(cartItem, CartItemDto.class);

    }
}
