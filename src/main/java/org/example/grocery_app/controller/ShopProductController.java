package org.example.grocery_app.controller;

import org.example.grocery_app.entities.ShopProduct;
import org.example.grocery_app.service.ShopProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shop-product")
public class ShopProductController {


    @Autowired
    private ShopProductService shopProductService;

    @PostMapping("/assign-product")
    public ResponseEntity<ShopProduct> assignProductToShopOwner(@RequestParam Long productId, @RequestParam Long shopkeeperId){
        ShopProduct shopProduct = this.shopProductService.assignProductToShopkeeper(productId, shopkeeperId);
        return  new ResponseEntity<>(shopProduct, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public  ResponseEntity<List<ShopProduct>> getAllShopProductByShopkeeperId(@RequestParam Long shopkeeperId){
        List<ShopProduct> productsByShopkeeper = this.shopProductService.getProductsByShopkeeper(shopkeeperId);
        return  new ResponseEntity<>(productsByShopkeeper, HttpStatus.OK);
    }
}
