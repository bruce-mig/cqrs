package com.github.bruce_mig.cqrs.product_query_service.controller;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.product_query_service.dto.ProductResponse;
import com.github.bruce_mig.cqrs.product_query_service.service.ProductQueryService;
import com.github.bruce_mig.cqrs.product_query_service.util.ProductMapper;
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
    public ResponseEntity<List<ProductResponse>> fetchAllProducts(){
        List<ProductDto> products = queryService.getProducts();

        return ResponseEntity.ok().body(ProductMapper.toHttpResponseList(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        ProductDto productDto = queryService.getProductById(id);

        return ResponseEntity.ok().body(ProductMapper.toHttpResponse(productDto));
    }
}
