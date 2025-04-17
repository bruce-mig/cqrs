package com.github.bruce_mig.cqrs.product_command_service.util;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.product_command_service.dto.ProductResponse;
import com.github.bruce_mig.cqrs.product_command_service.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    // Convert Product entity to ProductDto
    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );

    }

    // Convert ProductDto to Product entity
    public static Product toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName().toString())
                .description(productDto.getDescription().toString())
                .price(productDto.getPrice())
                .build();

    }

    // Convert ProductDto to ProductResponse
    public static ProductResponse toHttpResponse(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(productDto.getId())
                .name(productDto.getName().toString())
                .description(productDto.getDescription().toString())
                .price(productDto.getPrice())
                .build();

    }


    // Map a List of Product to a List of ProductDto
    public static List<ProductDto> toDtoList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    // Map a List of ProductDto to a List of Product
    public static List<Product> toEntityList(List<ProductDto> productDtos) {
        if (productDtos == null || productDtos.isEmpty()) {
            return List.of();
        }
        return productDtos.stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }

    // Map a List of ProductDto to a List of ProductResponse
    public static List<ProductResponse> toHttpResponseList(List<ProductDto> productDtos) {
        if (productDtos == null || productDtos.isEmpty()) {
            return List.of();
        }
        return productDtos.stream()
                .map(ProductMapper::toHttpResponse)
                .collect(Collectors.toList());
    }
}
