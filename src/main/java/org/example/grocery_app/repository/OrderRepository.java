package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
