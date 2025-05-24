package org.example.grocery_app.service;

import org.example.grocery_app.dto.AddProductRequestDto;
import org.example.grocery_app.dto.CategoryDto;

import java.util.HashMap;
import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto updateCategory(Long categoryId,CategoryDto categoryDto);
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long id);
    void deleteCategoryById(int id);
    CategoryDto addProductsToCategory(AddProductRequestDto addProductRequestDto);
    List<CategoryDto> getAllCategoriesName();
}
