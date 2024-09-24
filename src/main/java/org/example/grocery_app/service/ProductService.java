package org.example.grocery_app.service;

import org.example.grocery_app.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts();

}
