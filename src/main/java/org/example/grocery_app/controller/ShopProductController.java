package org.example.grocery_app.controller;

import org.example.grocery_app.entities.Product;
import org.example.grocery_app.entities.ShopProduct;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.service.ShopProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shop-product")
public class ShopProductController {


    @Autowired
    private ShopProductService shopProductService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/assign-product")
    public ResponseEntity<ShopProduct> assignProductToShopOwner(@RequestParam Long productId, @RequestParam Long shopkeeperId){
//        List<Product> all = this.productRepository.findAll();
//        all.forEach(a->{
//            Long id = a.getId();
//            ShopProduct shopProduct = this.shopProductService.assignProductToShopkeeper(id, shopkeeperId);
//
//        });

        ShopProduct shopProduct = this.shopProductService.assignProductToShopkeeper(productId, shopkeeperId);
        return  new ResponseEntity<>(shopProduct, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public  ResponseEntity<List<ShopProduct>> getAllShopProductByShopkeeperId(@RequestParam Long shopkeeperId){
        List<ShopProduct> productsByShopkeeper = this.shopProductService.getProductsByShopkeeper(shopkeeperId);
        return  new ResponseEntity<>(productsByShopkeeper, HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ShopProduct>> searchProduct(@RequestParam String productName){
        List<ShopProduct> shopProducts = this.shopProductService.searchShopProducts(productName);
        return new ResponseEntity<>(shopProducts, HttpStatus.OK);
    }

    @PutMapping("/update/{shopProductId}")
    ResponseEntity<ShopProduct> updateShopProduct(@PathVariable Long shopProductId, @RequestBody ShopProduct shopProduct ){
        ShopProduct shopProduct1 = this.shopProductService.updateShopProduct(shopProductId, shopProduct);
        return  new ResponseEntity<>(shopProduct, HttpStatus.OK);
    }
}
