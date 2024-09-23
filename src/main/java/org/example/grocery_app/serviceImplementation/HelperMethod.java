package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.CategoryDto;
import org.example.grocery_app.dto.InventoryDto;
import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.entities.Category;
import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.entities.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Product>changeProductDtoSetIntoProductEntitySet(Set<ProductDto> productDtoList) {
        Set<Product> ProductSet = productDtoList.stream()
                .map(productDto -> {
                    Product product = modelMapper.map(productDto, Product.class);
                    Inventory inventory = this.modelMapper.map(productDto.getInventoryDto(), Inventory.class);
                    product.setInventory(inventory);
                    return product;
                }).collect(Collectors.toSet());

        return ProductSet;
    }

    public Set<ProductDto> changeProductEntitySetIntoProductDtoSet(Set<Product> productList) {
        return productList.stream()
                .map(product -> {
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);
                    InventoryDto inventoryDto = this.modelMapper.map(product.getInventory(), InventoryDto.class);
                    productDto.setInventoryDto(inventoryDto);
                    return productDto;
                })
                .collect(Collectors.toSet());
    }

}
