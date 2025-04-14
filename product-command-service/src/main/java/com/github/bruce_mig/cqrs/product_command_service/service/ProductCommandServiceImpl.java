package com.github.bruce_mig.cqrs.product_command_service.service;

import com.github.bruce_mig.cqrs.product_command_service.dto.ProductDto;
import com.github.bruce_mig.cqrs.product_command_service.entity.Product;
import com.github.bruce_mig.cqrs.product_command_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_command_service.util.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository repository;

    public ProductCommandServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        Product savedProduct = repository.save(product);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + id + " not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());

        Product updatedProduct = repository.save(existingProduct);
        return ProductMapper.toDto(updatedProduct);
    }

}
