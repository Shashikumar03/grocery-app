package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

     Optional<Category> findByName(String name);
     @Query("SELECT id, c.name FROM Category c")
     List<Object[]> findAllCategoryIdAndNames();




}
