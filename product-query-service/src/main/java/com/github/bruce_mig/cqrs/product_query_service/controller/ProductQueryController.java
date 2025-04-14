package com.github.bruce_mig.cqrs.product_query_service.controller;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.product_query_service.service.ProductQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductQueryController {

    private final ProductQueryService queryService;

    public ProductQueryController(ProductQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> fetchAllProducts(){
        List<ProductDto> products = queryService.getProducts();
        return ResponseEntity.ok().body(products);
    }
}
