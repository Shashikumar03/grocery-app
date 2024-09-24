package org.example.grocery_app.controller;

import jakarta.validation.Valid;
import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        ProductDto productById = this.productService.getProductById(productId);
        return  new ResponseEntity<>(productById, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> allProducts = this.productService.getAllProducts();
        return  new ResponseEntity<>(allProducts, HttpStatus.OK);
    }
    @PutMapping("update/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId,  @Valid  @RequestBody ProductDto productDto){
        ProductDto responsepProductDto = this.productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(responsepProductDto, HttpStatus.OK);
    }
}
