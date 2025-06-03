package org.example.grocery_app.serviceImplementation;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.*;
import org.example.grocery_app.entities.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HelperMethod {

    @Autowired
    private ModelMapper modelMapper;

    public Set<Category> changeCategoryDtoSetIntoCategoryEntityList(Set<CategoryDto> categoryDtoList) {
        return categoryDtoList.stream()
                .map(categoryDto -> modelMapper.map(categoryDto, Category.class))
                .collect(Collectors.toSet());
    }

    public Set<CategoryDto> changeCategoryEntitySetIntoCategoryDtoList(Set<Category> categoryList) {
        return categoryList.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toSet());
    }

    public Set<Product>changeProductDtoSetIntoProductEntitySet(Set<ProductDto> productDtoList, Category category) {
        Set<Product> ProductSet = productDtoList.stream()
                .map(productDto -> {
                    Product product = modelMapper.map(productDto, Product.class);
                    Inventory inventory = this.modelMapper.map(productDto.getInventoryDto(), Inventory.class);
                    product.setInventory(inventory);
                    inventory.setProduct(product);
                    product.setCategory(category);

                    return product;
                }).collect(Collectors.toSet());

        return ProductSet;
    }

    public Set<ProductDto> changeProductEntitySetIntoProductDtoSet(Set<Product> productList) {
        return productList.stream()
                .map(product -> {
                    log.info("product info :{}", product);
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);
                    log.info("product dto :{}", productDto);
                    InventoryDto inventoryDto = this.modelMapper.map(product.getInventory(), InventoryDto.class);
                    productDto.setInventoryDto(inventoryDto);
                    return productDto;
                })
                .collect(Collectors.toSet());
    }

    public  CartDto changeCartIntoCartDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setCartId(cart.getId());
        Set<CartItem> cartItems = cart.getCartItems();
        Set<CartItemDto> setOfCartItemDto = cartItems.stream().map(item -> {
            Product product = item.getProduct();
//            ProductDto productDto = this.modelMapper.map(product, ProductDto.class);
            CartItemDto cartItemDto = this.modelMapper.map(item, CartItemDto.class);
            cartItemDto.setCartItemId(item.getId());
            cartItemDto.setProductName(product.getName());
            cartItemDto.setImageUrl(product.getImageUrl());

            return cartItemDto;
        }).collect(Collectors.toSet());
       cartDto.setCartItemsDto(setOfCartItemDto);
       return cartDto;

    }

    public CartItemDto changeCartItemIntoCartItemDto(CartItem cartItem) {
        CartItemDto cartItemDto = modelMapper.map(cartItem, CartItemDto.class);
        cartItemDto.setCartItemId(cartItem.getId());
        return cartItemDto;
    }

//    public OrderDto changeOrderIntoOrderDto(Order order) {
//        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
//
////        Payment payment = order.getPayment();
//        PaymentDto paymentDto = this.modelMapper.map(payment, PaymentDto.class);
////        paymentDto.setOrderDto(orderDto);
//        orderDto.setPaymentDto(paymentDto);
//
//        Delivery delivery = order.getDelivery();
//        DeliveryDto deliveryDto = this.modelMapper.map(delivery, DeliveryDto.class);
////        deliveryDto.setOrderDto(orderDto);
//        orderDto.setDeliveryDto(deliveryDto);
//
//        Cart cart = order.getCart();
//
//        CartDto cartDto = changeCartIntoCartDto(cart);
//        orderDto.setCartDto(cartDto);
//
//        return orderDto;
//    }

}


