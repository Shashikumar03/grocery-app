package org.example.grocery_app.service;

import org.example.grocery_app.dto.DeliveryAddressDto;

import java.util.List;

public interface DeliveryAddressService {


    DeliveryAddressDto createNewDeliveryAddress(Long userId, DeliveryAddressDto deliveryAddressDto);
    List<DeliveryAddressDto> getUserAllDeliveryAddresses(Long userId);

    DeliveryAddressDto updateDeliveryAddress(Long deliveryAddressId, DeliveryAddressDto deliveryAddressDto);
    DeliveryAddressDto getAddressByAddressId(Long addressId);
}
