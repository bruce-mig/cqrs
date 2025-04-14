package com.github.bruce_mig.cqrs.product_command_service.service;


import com.github.bruce_mig.cqrs.payload.ProductDto;

public interface ProductCommandService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDto);
}
