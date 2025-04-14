package com.github.bruce_mig.cqrs.product_query_service.service;

import com.github.bruce_mig.cqrs.product_query_service.dto.ProductDto;

import java.util.List;

public interface ProductQueryService {

    List<ProductDto> getProducts();
}
