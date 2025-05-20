package org.example.grocery_app.serviceImplementation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.dto.CartDto;
import org.example.grocery_app.dto.CartItemDto;
import org.example.grocery_app.dto.UserDto;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CartItemRepository;
import org.example.grocery_app.repository.CartRepository;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.security.JwtHelper;
//import org.example.grocery_app.security.SecurityUtils;
import org.example.grocery_app.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private HelperMethod helperMethod;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private HttpServletRequest request;

//    @Autowired
//    private SecurityUtils securityUtils;




    @Override
    public CartDto addProductToCart(Long userId, CartItemDto cartItemDto) {
        System.out.println("shashi");

//        this.securityUtils.validateUserAccess(userId, request);
        // Fetch the user from the repository
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));



        // Fetch the product from the repository
        Product product = this.productRepository.findById(cartItemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", cartItemDto.getProductId()));
        if(!product.isAvailable()){
            throw  new ApiException(" product is OUT OF STOCK");
        }
        Inventory inventory = product.getInventory();

        // Check if the user already has a cart
        Optional<Cart> optionalCart = cartRepository.findByUserAndStatus(user,CartStatus.ACTIVE);
        Cart cart;

        // If the cart doesn't exist, create a new one
        if (optionalCart.isEmpty()) {
            log.info("No existing cart found for user ID {}. Creating a new cart: {}", userId, cartItemDto);
            cart = new Cart();
            cart.setUser(user);
            cart.setStatus(CartStatus.ACTIVE);
            cart.setCartItems(new HashSet<>());

            // Save the cart FIRST to avoid TransientObjectException
            cart = cartRepository.save(cart);
        } else {
            cart = optionalCart.get();
            cart.setStatus(CartStatus.ACTIVE);
        }

        // Check if the product is already in the cart
        Set<CartItem> items = cart.getCartItems();
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            // If the product already exists in the cart, update the quantity
            cartItem = optionalCartItem.get();
            int newQuantity = cartItem.getQuantity() + cartItemDto.getQuantity();
            if (inventory.getStockQuantity() < newQuantity) {
                log.info("This product is out of stock: {}", product);
                throw new ApiException("Out of stock");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            // Otherwise, create a new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemDto.getQuantity());

            if (inventory.getStockQuantity() < cartItem.getQuantity()) {
                log.info("This product is out of stock: {}", product);
                throw new ApiException("Out of stock");
            }

            // Add the new cart item to the cart
            items.add(cartItem);

            // Save the cartItem explicitly if it has a separate repository (to avoid TransientObjectException)
//            cartItemRepository.save(cartItem);
        }

        // Save the cart with the updated items (this might cascade save the cart items as well)
        cart.setCartItems(items);
        Cart savedCart = cartRepository.save(cart);

        // Convert saved cart to CartDto and return
        CartDto cartDto = this.modelMapper.map(savedCart, CartDto.class);
        UserDto userDto = this.modelMapper.map(savedCart.getUser(), UserDto.class);
        cartDto.setUserDto(userDto);

        Set<CartItemDto> cartItemDtos = savedCart.getCartItems().stream()
                .map(cartItemEntity -> {
                    CartItemDto map = this.modelMapper.map(cartItemEntity, CartItemDto.class);
                    map.setCartItemId(cartItemEntity.getId());
                    return  map;
                })
                .collect(Collectors.toSet());
        cartDto.setCartItemsDto(cartItemDtos);
        return cartDto;
    }


    @Override
    public CartDto removeProductFromCart(Long userId, Long productId) {

//        this.securityUtils.validateUserAccess(userId,this.request);

        // Fetch the user from the repository
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Find the active cart for the user
        Cart cart = this.cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        // Get the existing cart items
        Set<CartItem> cartItems = cart.getCartItems();

        // Find the cart item with the matching product ID
        CartItem itemToRemove = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null); // Use null to indicate item was not found

        // If the item exists, remove it
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove); // Remove the found item
        } else {
            // Handle the case where the product is not found in the cart
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }

        // Save the updated cart to persist changes
        cart.setCartItems(cartItems);
        Cart savedCart = this.cartRepository.save(cart);

        // Convert the saved cart to CartDto
        CartDto cartDto = this.modelMapper.map(savedCart, CartDto.class);
        cartDto.setCartTotalPrice(savedCart.getTotalPricesOfAllProduct());

        // Convert User entity to UserDto
        UserDto userDto = this.modelMapper.map(savedCart.getUser(), UserDto.class);
        cartDto.setUserDto(userDto);

        // Convert CartItems to CartItemDto and set in CartDto
        Set<CartItemDto> cartItemDtoSet = savedCart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemDto map = this.modelMapper.map(cartItem, CartItemDto.class);
                    map.setCartItemId(cartItem.getId());
                    return map;
                })
                .collect(Collectors.toSet());
        cartDto.setCartItemsDto(cartItemDtoSet);

        return cartDto;
    }

    @Override
    public CartDto viewUserCart(Long userId) {
//        this.securityUtils.validateUserAccess(userId,request);
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Cart cart = this.cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow(() -> new ApiException("no cart found for this user " + userId));
//        System.out.println(cart);
//        Optional<Cart> cartByUserAndStatus = this.cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow(()-> new ApiException("no cart foun"));

        CartDto cartDto = this.helperMethod.changeCartIntoCartDto(cart);
        cartDto.setCartTotalPrice((cart.getTotalPricesOfAllProduct()));

//        System.out.println(cartDto);
        log.info("cartDto :{}", cartDto);
        return cartDto;


    }

    public void updateCartDiscount(Long cartId, double newDiscountAmount) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setDiscountAmount(newDiscountAmount);
        cartRepository.save(cart);
    }

}
