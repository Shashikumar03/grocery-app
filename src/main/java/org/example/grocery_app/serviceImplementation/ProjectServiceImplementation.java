package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.CategoryDto;
import org.example.grocery_app.dto.InventoryDto;
import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.service.ProductService;
import org.example.grocery_app.util.ProductSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImplementation implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modalMapper;

    @Autowired
    private HelperMethod helperMethod;

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = this.productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        InventoryDto inventoryDto  = this.modalMapper.map(product.getInventory(), InventoryDto.class);
        ProductDto productDto1 = this.modalMapper.map(product, ProductDto.class);
        productDto1.setInventoryDto(inventoryDto);
        return productDto1;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        Set<Product> listOfProduct = this.productRepository.findAll().stream().collect(Collectors.toSet());
        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(listOfProduct);
        return productDtos.stream().collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long productId,ProductDto productDto) {
        Product product = this.productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));
        Inventory inventory = product.getInventory();
        InventoryDto updateInventoryDto = productDto.getInventoryDto();

//        update inventory 
        inventory.setReservedStock(updateInventoryDto.getReservedStock());
        inventory.setStockQuantity(updateInventoryDto.getStockQuantity());
        inventory.setProduct(product);
        // update the product also
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setAvailable(productDto.isAvailable());
        product.setImageUrl(productDto.getImageUrl());
        Product productSaved = this.productRepository.save(product);
        product.setInventory(inventory);

        InventoryDto inventoryDto  = this.modalMapper.map(productSaved.getInventory(), InventoryDto.class);

        ProductDto productDto1 = this.modalMapper.map(productSaved, ProductDto.class);
        productDto1.setId(product.getId());
        inventoryDto.setId(inventory.getId());
        productDto1.setInventoryDto(inventoryDto);
        System.out.println(productDto1);
        return productDto1;
    }

    @Override
    public List<ProductDto> searchProducts(String name, String category, Double minPrice, Double maxPrice) {
        Specification<Product> spec = ProductSpecification.getProductSpecs(name, category, minPrice, maxPrice);
        List<Product> products = productRepository.findAll(spec);
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = modalMapper.map(product, ProductDto.class);
        InventoryDto inventoryDto = modalMapper.map(product.getInventory(), InventoryDto.class);
        CategoryDto categoryDto= modalMapper.map(product.getCategory(), CategoryDto.class);
        productDto.setInventoryDto(inventoryDto);
        productDto.setCategoryDto(categoryDto);
        return productDto;
    }


}
