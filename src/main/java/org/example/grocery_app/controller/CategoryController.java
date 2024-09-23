package org.example.grocery_app.controller;

import jakarta.validation.Valid;
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
    public  ResponseEntity<CategoryDto> createCategory( @RequestBody CategoryDto categoryDto){
        CategoryDto category = this.categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(category,HttpStatus.CREATED);
    }



}
