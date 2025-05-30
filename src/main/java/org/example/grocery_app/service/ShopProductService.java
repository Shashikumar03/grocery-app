package org.example.grocery_app.service;

import org.example.grocery_app.entities.ShopProduct;

import java.util.List;

public interface ShopProductService {

    ShopProduct assignProductToShopkeeper(Long productId, Long shopkeeperId);
    ShopProduct updateShopProduct(Long shopProductId, double price, boolean available, String unit);
    List<ShopProduct> getProductsByShopkeeper(Long shopkeeperId);
    ShopProduct getShopProductById(Long id);
}
