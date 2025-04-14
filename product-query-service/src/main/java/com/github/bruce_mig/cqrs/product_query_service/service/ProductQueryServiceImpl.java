package com.github.bruce_mig.cqrs.product_query_service.service;

import com.github.bruce_mig.cqrs.product_query_service.dto.ProductDto;
import com.github.bruce_mig.cqrs.product_query_service.entity.Product;
import com.github.bruce_mig.cqrs.product_query_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_query_service.util.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository repository;

    public ProductQueryServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<ProductDto> getProducts() {
        List<Product> productList = repository.findAll();
        return ProductMapper.toDtoList(productList);
    }
}
