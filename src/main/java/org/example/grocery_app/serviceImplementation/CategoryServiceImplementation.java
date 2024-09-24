package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.CategoryDto;
import org.example.grocery_app.dto.InventoryDto;
import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.entities.Category;
import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CategoryRepository;
import org.example.grocery_app.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HelperMethod helperMethod;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Set<ProductDto> productsDtoSet = categoryDto.getProductsDto();
        Category category = this.modelMapper.map(categoryDto, Category.class);

        Set<Product> products = helperMethod.changeProductDtoSetIntoProductEntitySet(productsDtoSet);
        category.setProducts(products);
        Category saveCategory = this.categoryRepository.save(category);

        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(saveCategory.getProducts());
        CategoryDto saveCategoryDto = this.modelMapper.map(saveCategory, CategoryDto.class);
        saveCategoryDto.setProductsDto(productDtos);
        return saveCategoryDto;
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        // step 1 find the category
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
//        step 2 find what to update
        Map<Long,ProductDto>updateProductsDto= new HashMap<>();
        Map<Long, InventoryDto>updateInventoryDto= new HashMap<>();
        categoryDto.getProductsDto().forEach(productDto -> {
            updateProductsDto.put(productDto.getId(), productDto);
            InventoryDto inventoryDto = productDto.getInventoryDto();
            updateInventoryDto.put(inventoryDto.getId(), inventoryDto);

        });
//        Set<Product> updateProducts = category.getProducts();
        //step 3 verify and update the product and inventory
        Set<Product> updateProducts=category.getProducts().stream().map(product -> {
            System.out.println("hello");
            boolean requireUpdate = updateProductsDto.containsKey(product.getId());
            if (requireUpdate) {
                ProductDto product1 = updateProductsDto.get(product.getId());
                product.setName(product1.getName());
                product.setDescription(product1.getDescription());
                product.setPrice(product1.getPrice());
                product.setImageUrl(product1.getImageUrl());
                product.setAvailable(product1.isAvailable());

            }
            Inventory inventory = product.getInventory();
            boolean requireInventryUpdate = updateInventoryDto.containsKey(inventory.getId());

            if (requireInventryUpdate) {
                InventoryDto inventory1 = updateInventoryDto.get(inventory.getId());
                inventory.setReservedStock(inventory1.getReservedStock());
                inventory.setReservedStock(inventory1.getReservedStock());

            }
            product.setInventory(inventory);
//            updateProducts.add(product);
            System.out.println("shyam"+product);
        return product;

        }).collect(Collectors.toSet());
//        step 4 update the category
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setProducts(updateProducts);
        System.out.println("shashi"+updateProducts);


        Category saveCategory = this.categoryRepository.save(category);
        System.out.println(saveCategory);

//      step 5 changing entity into dto
        Set<Product> saveProducts = saveCategory.getProducts();

        Set<ProductDto> productDtos = helperMethod.changeProductEntitySetIntoProductDtoSet(saveProducts);
        CategoryDto saveCategoryDto = this.modelMapper.map(saveCategory, CategoryDto.class);
        saveCategoryDto.setProductsDto(productDtos);
        return saveCategoryDto;
//        return null;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return List.of();
    }

    @Override
    public CategoryDto getCategoryById(int id) {
        return null;
    }

    @Override
    public void deleteCategoryById(int id) {

    }
}
