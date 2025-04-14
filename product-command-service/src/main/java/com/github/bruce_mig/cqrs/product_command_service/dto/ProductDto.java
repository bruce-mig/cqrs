package com.github.bruce_mig.cqrs.product_command_service.dto;


public record ProductDto(
        Long id,
        String name,
        String description,
        Double price
) {
}
