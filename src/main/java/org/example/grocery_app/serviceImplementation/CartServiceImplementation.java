package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.dto.CartDto;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.CartItem;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartItemRepository;
import org.example.grocery_app.repository.CartRepository;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto addProductToCart(Long userId, CartItemDto cartItemDto) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Product product = this.productRepository.findById(cartItemDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", cartItemDto.getProductId()));
        // Check if the user already has a cart
        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        Cart cart;
        CartItem cartItem1 = this.modelMapper.map(cartItemDto, CartItem.class);

        // If the cart doesn't exist, create a new one
        if (optionalCart.isEmpty()) {
            HashSet<CartItem> setOfCartItems= new HashSet<>();
            setOfCartItems.add(cartItem1);
            cart = new Cart();
            cart.setUser(user);
            cart.setStatus(CartStatus.ACTIVE);
            cart.setCartItems(setOfCartItems);
            cartRepository.save(cart);
        } else {
            cart = optionalCart.get();
            cart.setStatus(CartStatus.ACTIVE);
        }

        // Check if the product is already in the cart
        Set<CartItem> items = cart.getCartItems();

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem;
//
        if (optionalCartItem.isPresent()) {
            // If the product already exists in the cart, update the quantity
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
            items.add(cartItem);
        } else {
            // Otherwise, create a new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItem.getQuantity()+cartItemDto.getQuantity());
            items.add(cartItem);
        }
//
//        // Save the cart item
        cart.setCartItems(items);
        Cart saveCart = this.cartRepository.save(cart);


//        converting savecart into cartDto
        CartDto cartDto = this.modelMapper.map(saveCart, CartDto.class);
        User user1 = saveCart.getUser();

        Set<CartItem> cartItems = saveCart.getCartItems();

        UserDto userDto = this.modelMapper.map(user1, UserDto.class);
        cartDto.setUserDto(userDto);
        Set<CartItemDto> setOfcartItemDto = cartItems.stream().map(cartItem11 -> this.modelMapper.map(cartItem11, CartItemDto.class)).collect(Collectors.toSet());
        cartDto.setCartItemsDto(setOfcartItemDto);

        return cartDto;
    }
}
