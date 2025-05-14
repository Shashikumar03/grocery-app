package org.example.grocery_app.controller;


import org.example.grocery_app.dto.DeliveryAddressDto;
import org.example.grocery_app.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("getAll/{userId}")
    public  ResponseEntity<List<DeliveryAddressDto>> getAllDeliveryAddress(@PathVariable Long userId){
        List<DeliveryAddressDto> userAllDeliveryAddresses = this.deliveryAddressService.getUserAllDeliveryAddresses(userId);
        return  new ResponseEntity<>(userAllDeliveryAddresses, HttpStatus.OK);
    }

    @PutMapping("update/{deliveryAddressId}")
    public  ResponseEntity<DeliveryAddressDto> updateDeliveryAddress(@PathVariable Long deliveryAddressId, @Validated @RequestBody DeliveryAddressDto deliveryAddressDto){
        DeliveryAddressDto deliveryAddressDto1 = this.deliveryAddressService.updateDeliveryAddress(deliveryAddressId, deliveryAddressDto);
        return  new ResponseEntity<>(deliveryAddressDto1, HttpStatus.OK);
    }

    @GetMapping("byAddressId/{addressId}")
    public  ResponseEntity<DeliveryAddressDto> getAddressById(@PathVariable Long addressId){
        DeliveryAddressDto addressByAddressId = this.deliveryAddressService.getAddressByAddressId(addressId);

        return new ResponseEntity<>(addressByAddressId,HttpStatus.OK);
    }
}
