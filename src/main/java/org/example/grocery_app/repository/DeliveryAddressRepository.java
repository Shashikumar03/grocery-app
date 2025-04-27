package org.example.grocery_app.repository;

import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.entities.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    DeliveryAddressDto findByDeliveryAddressId(Long deliveryAddressId);


}
