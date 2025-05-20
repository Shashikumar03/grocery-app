package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

     Optional<Category> findByName(String name);
}
