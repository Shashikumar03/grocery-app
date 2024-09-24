package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.ProductDto;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.repository.ProductRepository;
import org.example.grocery_app.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

        return null;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        Set<Product> listOfProduct = this.productRepository.findAll().stream().collect(Collectors.toSet());
        Set<ProductDto> productDtos = this.helperMethod.changeProductEntitySetIntoProductDtoSet(listOfProduct);
        return productDtos.stream().collect(Collectors.toList());
    }
}
