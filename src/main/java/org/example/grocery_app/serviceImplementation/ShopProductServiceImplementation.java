package org.example.grocery_app.serviceImplementation;

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
}
