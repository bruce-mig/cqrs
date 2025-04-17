package com.github.bruce_mig.cqrs.product_command_service.controller;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.product_command_service.dto.ProductResponse;
import com.github.bruce_mig.cqrs.product_command_service.service.ProductCommandService;
import com.github.bruce_mig.cqrs.product_command_service.util.ProductMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
public class ProductCommandController {

    private final ProductCommandService commandService;

    public ProductCommandController(ProductCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductDto productDto){
        ProductDto product = commandService.createProduct(productDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Gets the current request URI
                .path("/{id}")         // Appends the new resource ID to the path
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity.created(location).body(ProductMapper.toHttpResponse(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                    @RequestBody ProductDto productDto){
        ProductDto updateProduct = commandService.updateProduct(id, productDto);
        return ResponseEntity.ok().body(ProductMapper.toHttpResponse(updateProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        commandService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
