package org.example.grocery_app.controller;

import org.example.grocery_app.dto.ShopkeeperDto;
import org.example.grocery_app.service.ShopkeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shopkeeper")
public class ShopkeeperController {


    @Autowired
    private ShopkeeperService shopkeeperService;



    @PostMapping("/")
    public ResponseEntity<ShopkeeperDto> createShopKeeper(@RequestBody ShopkeeperDto shopkeeperDto){
        ShopkeeperDto shopkeeper = this.shopkeeperService.createShopkeeper(shopkeeperDto);
        return  new ResponseEntity<>(shopkeeper, HttpStatus.OK);


    }
}
