package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopkeeperRepository extends JpaRepository<Shopkeeper , Long> {
}
