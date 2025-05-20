package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {
    Set<Inventory> findAllByProductIdIn(Set<Long> productIds);
}
