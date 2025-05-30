package org.example.grocery_app.repository;

import org.example.grocery_app.entities.PriceSettlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceSettlementRepository extends JpaRepository<PriceSettlement, Long> {

    List<PriceSettlement> findAllBySettlementDateBetween(LocalDateTime start, LocalDateTime end);

}
