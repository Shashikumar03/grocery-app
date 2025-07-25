package org.example.grocery_app.repository;

import org.example.grocery_app.entities.FeeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeTableRepository extends JpaRepository<FeeTable, Long> {
}
