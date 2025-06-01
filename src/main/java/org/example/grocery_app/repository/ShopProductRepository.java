package org.example.grocery_app.repository;

import org.example.grocery_app.entities.ShopProduct;
import org.example.grocery_app.entities.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    Optional<ShopProduct> findByProductIdAndShopkeeperId(Long productId,Long ShopkeeperId);

    List<ShopProduct> findByShopkeeperId(Long shopkeeperId);

    Optional<ShopProduct> findByProductId(Long productId);

    List<ShopProduct> findByProduct_NameContainingIgnoreCase(String name);




}
