package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.CategoryDto;
import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.entities.Category;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.CategoryRepository;
import org.example.grocery_app.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
        Set<ProductDto> productsDtoSet = categoryDto.getProducts();
        Category category = this.modelMapper.map(categoryDto, Category.class);

        Set<Product> products = helperMethod.changeProductDtoSetIntoProductEntitySet(productsDtoSet);
        category.setProducts(products);
        Category saveCategory = this.categoryRepository.save(category);

        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(saveCategory.getProducts());
        CategoryDto saveCategoryDto = this.modelMapper.map(saveCategory, CategoryDto.class);
        saveCategoryDto.setProducts(productDtos);
        return saveCategoryDto;
    }

    @Override
    public CategoryDto updateCategory(Long categoryId,CategoryDto categoryDto) {
        this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
        return null;
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
