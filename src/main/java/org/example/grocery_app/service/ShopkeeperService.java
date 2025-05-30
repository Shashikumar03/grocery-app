package org.example.grocery_app.service;

import org.example.grocery_app.dto.ShopkeeperDto;
import org.example.grocery_app.entities.Shopkeeper;

import java.util.List;
import java.util.Optional;

public interface ShopkeeperService {
    ShopkeeperDto createShopkeeper(ShopkeeperDto shopkeeper);
    Optional<ShopkeeperDto> getShopkeeperById(Long id);
    List<ShopkeeperDto> getAllShopkeepers();
    void deleteShopkeeper(Long id);
}
