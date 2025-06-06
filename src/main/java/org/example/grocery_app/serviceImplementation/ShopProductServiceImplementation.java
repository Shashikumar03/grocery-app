package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.entities.ShopProduct;
import org.example.grocery_app.entities.Shopkeeper;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.repository.ShopProductRepository;
import org.example.grocery_app.repository.ShopkeeperRepository;
import org.example.grocery_app.service.ShopProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShopProductServiceImplementation implements ShopProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopkeeperRepository shopkeeperRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;


    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShopProduct assignProductToShopkeeper(Long productId, Long shopkeeperId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Shopkeeper shopkeeper = shopkeeperRepository.findById(shopkeeperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopkeeper", "shopkeeperId", shopkeeperId));

        // Optional: Check if already assigned
        Optional<ShopProduct> existingAssignment = shopProductRepository
                .findByProductIdAndShopkeeperId(productId, shopkeeperId);
        if (existingAssignment.isPresent()) {
            throw new ApiException("Product already assigned to this shopkeeper.");
        }

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setShopkeeper(shopkeeper);
        shopProduct.setProduct(product);
        shopProduct.setAvailable(true);
        shopProduct.setSuggestedPrice(product.getPrice() - 5);
        shopProduct.setProductAssignedTime(LocalDateTime.now()); // <-- Ensure this field exists

        return shopProductRepository.save(shopProduct);
    }


    @Override
    public ShopProduct updateShopProduct(Long shopProductId, double price, boolean available, String unit) {
        return null;
    }

    @Override
    public List<ShopProduct> getProductsByShopkeeper(Long shopkeeperId) {
        List<ShopProduct> byShopkeeperId = this.shopProductRepository.findByShopkeeperId(shopkeeperId);

        return byShopkeeperId;
    }

    @Override
    public ShopProduct getShopProductById(Long id) {
        return null;
    }

    @Override
    public List<ShopProduct> searchShopProducts(String name) {
        return  this.shopProductRepository.findByProduct_NameContainingIgnoreCase(name);

    }

    @Override
    public ShopProduct updateShopProduct(Long shopProductId, ShopProduct updatedShopProduct) {
        // Fetch existing ShopProduct by ID (throws if not found)
        ShopProduct existingShopProduct = shopProductRepository.findById(shopProductId)
                .orElseThrow(() -> new ResourceNotFoundException("ShopProduct", "id", shopProductId));

        // Update ShopProduct fields
        existingShopProduct.setSuggestedPrice(updatedShopProduct.getSuggestedPrice());
        existingShopProduct.setAvailable(updatedShopProduct.isAvailable());
//        existingShopProduct.setProductAssignedTime(updatedShopProduct.getProductAssignedTime());
        existingShopProduct.setProductUpdatedTime(LocalDateTime.now());

        // Update related Product entity
        Product existingProduct = existingShopProduct.getProduct();
        Product updatedProduct = updatedShopProduct.getProduct();

        if (updatedProduct != null) {
//            existingProduct.setName(updatedProduct.getName());
//            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
//            existingProduct.setImageUrl(updatedProduct.getImageUrl());
            existingProduct.setAvailable(updatedProduct.isAvailable());
//            existingProduct.setUnit(updatedProduct.getUnit());
            // Note: You might want to handle category updates separately if needed
        }
            existingProduct.setAvailable(updatedProduct.isAvailable());


        // Update Inventory entity
        Inventory existingInventory = existingProduct.getInventory();
        Inventory updatedInventory = updatedProduct != null ? updatedProduct.getInventory() : null;

        if (existingInventory != null && updatedInventory != null) {
            existingInventory.setStockQuantity(updatedInventory.getStockQuantity());
            existingInventory.setReservedStock(updatedInventory.getReservedStock());
        }

        // Save the updated ShopProduct (cascade should save Product and Inventory if configured)
        return shopProductRepository.save(existingShopProduct);
    }

}
