package org.example.grocery_app.repository;

import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.entities.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    Optional<DeliveryAddress>findByDeliveryAddressId(Long deliveryAddressId);

    List<DeliveryAddress> findByUserId(Long userId);




}
