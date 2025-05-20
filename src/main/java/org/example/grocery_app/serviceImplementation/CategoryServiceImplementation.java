package org.example.grocery_app.serviceImplementation;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.AddProductRequestDto;
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
import org.springframework.cache.annotation.Cacheable;


@Service
@Slf4j
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

        Set<Product> products = helperMethod.changeProductDtoSetIntoProductEntitySet(productsDtoSet, category);
        products.forEach(product ->product.setCategory(category));
        category.setProducts(products);
        Category saveCategory = this.categoryRepository.save(category);

        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(saveCategory.getProducts());
        CategoryDto saveCategoryDto = this.modelMapper.map(saveCategory, CategoryDto.class);
        saveCategoryDto.setProductsDto(productDtos);
        return saveCategoryDto;
//        System.out.println(products);
//        return  null;
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
//            System.out.println("hello");
            boolean requireUpdate = updateProductsDto.containsKey(product.getId());
            if (requireUpdate) {
                ProductDto product1 = updateProductsDto.get(product.getId());
                product.setCategory(category);
                product.setName(product1.getName());
                product.setDescription(product1.getDescription());
                product.setPrice(product1.getPrice());
                product.setImageUrl(product1.getImageUrl());
                product.setAvailable(product1.isAvailable());

            }
            Inventory inventory = product.getInventory();
            boolean requireInventoryUpdate = updateInventoryDto.containsKey(inventory.getId());
            log.info("checking inventory id exist or not : {}",requireInventoryUpdate);
            if (requireInventoryUpdate) {
                InventoryDto inventory1 = updateInventoryDto.get(inventory.getId());
                inventory.setStockQuantity(inventory1.getStockQuantity());
                inventory.setReservedStock(inventory1.getReservedStock());
                System.out.println("shashi kumar kushwaha");
                System.out.println(inventory1);
                System.out.println(inventory);

            }
            product.setInventory(inventory);
            inventory.setProduct(product);

//            System.out.println("shyam"+product);
        return product;

        }).collect(Collectors.toSet());
//        step 4 update the category
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setProducts(updateProducts);
        System.out.println("shashi"+updateProducts);


        Category saveCategory = this.categoryRepository.save(category);
//        System.out.println(saveCategory);

//      step 5 changing entity into dto
        Set<Product> saveProducts = saveCategory.getProducts();

        Set<ProductDto> productDtos = helperMethod.changeProductEntitySetIntoProductDtoSet(saveProducts);
        CategoryDto saveCategoryDto = this.modelMapper.map(saveCategory, CategoryDto.class);
        saveCategoryDto.setProductsDto(productDtos);
        return saveCategoryDto;
//        return null;
    }

    @Override
//    @Cacheable(value = "categories")
    public List<CategoryDto> getAllCategories() {
        log.info("Fetching from the Database");
        List<Category> listOfCategory = this.categoryRepository.findAll();
        return listOfCategory.stream().map(category -> {
            CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);
            Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(category.getProducts());
            categoryDto.setProductsDto(productDtos);
            return categoryDto;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        // step 1 find the category
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        // step 2 find products and change into product dto
        Set<Product> products = category.getProducts();
        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(products);
        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);
        categoryDto.setProductsDto(productDtos);
        return categoryDto;
    }

    @Override
    public void deleteCategoryById(int id) {

    }

    public CategoryDto addProductsToCategory(AddProductRequestDto addProductRequestDto) {
        // Step 1: Retrieve the category
        Category category = this.categoryRepository.findById(addProductRequestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", addProductRequestDto.getCategoryId()));

        // Step 2: Convert incoming ProductDto to Product entities
        Set<Product> products = this.helperMethod.changeProductDtoSetIntoProductEntitySet(addProductRequestDto.getProductsDto(), category);

        // Step 3: Add the new products to the existing set of products in the category
        category.getProducts().addAll(products);

        // Step 4: Save the updated category along with the new products
        Category updatedCategory = this.categoryRepository.save(category);
        System.out.println(updatedCategory);

//        // Step 5: Convert the updated category back to a CategoryDto
        CategoryDto updatedCategoryDto = this.modelMapper.map(updatedCategory, CategoryDto.class);
        Set<Product> products1 = updatedCategory.getProducts();
        System.out.println(products1);
        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(products1);
        System.out.println(productDtos);
        updatedCategoryDto.setProductsDto(productDtos);
//        // Step 6: Return the updated category
        return updatedCategoryDto;
//        return null;
    }
}
