package org.example.grocery_app.controller;

import jakarta.validation.Valid;
import org.example.grocery_app.dto.AddProductRequestDto;
import org.example.grocery_app.dto.CategoryDto;
import org.example.grocery_app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")

public class CategoryController {
    @Autowired
    private  CategoryService categoryService;

    @PostMapping("/")
    public  ResponseEntity<CategoryDto> createCategory( @RequestBody @Valid CategoryDto categoryDto){
        CategoryDto category = this.categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(category,HttpStatus.CREATED);
    }
    @PutMapping("/update/{categoryId}")
    public  ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto){
        CategoryDto responseCategoryDto = this.categoryService.updateCategory(categoryId, categoryDto);
        return new ResponseEntity<>(responseCategoryDto,HttpStatus.OK);
    }
    @GetMapping("/{categoryId}")
    public  ResponseEntity<CategoryDto> getCategory(@PathVariable Long categoryId){
        CategoryDto categoryById = this.categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(categoryById,HttpStatus.OK);
    }
    @GetMapping("/all")
    public  ResponseEntity<List<CategoryDto>> getAllCategory(){
        List<CategoryDto> allCategories = this.categoryService.getAllCategories();
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }
    @PostMapping("/add-products")
    public ResponseEntity<CategoryDto> addProductsToCategory( @Valid  @RequestBody AddProductRequestDto addProductRequestDto) {
        CategoryDto updatedCategoryDto = this.categoryService.addProductsToCategory(addProductRequestDto);
        return new ResponseEntity<>(updatedCategoryDto, HttpStatus.OK);
    }



}
