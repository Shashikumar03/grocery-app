package org.example.grocery_app.controller;


import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-address")

public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;


    @PostMapping("/{userId}")
    public ResponseEntity<DeliveryAddressDto> createNewDeliveryAddress(@PathVariable Long userId, @Validated @RequestBody DeliveryAddressDto deliveryAddressDto){
        DeliveryAddressDto newDeliveryAddress = this.deliveryAddressService.createNewDeliveryAddress(userId, deliveryAddressDto);
        return  new ResponseEntity<>(newDeliveryAddress, HttpStatus.CREATED);
    }
}
