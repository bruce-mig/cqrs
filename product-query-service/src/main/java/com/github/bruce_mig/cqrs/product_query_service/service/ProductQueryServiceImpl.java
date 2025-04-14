package com.github.bruce_mig.cqrs.product_query_service.service;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.payload.ProductEvent;
import com.github.bruce_mig.cqrs.product_query_service.entity.Product;
import com.github.bruce_mig.cqrs.product_query_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_query_service.util.KafkaEventType;
import com.github.bruce_mig.cqrs.product_query_service.util.ProductMapper;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "${topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void processProductEvents(ProductEvent productEvent) {
        ProductDto productDto = productEvent.getProductDto();
        Product product = ProductMapper.toEntity(productDto);
        Long id = productDto.getId();

        if (productEvent.getEventType().equals(KafkaEventType.CREATE_PRODUCT.name())) {
            repository.save(product);

        } else if (productEvent.getEventType().equals(KafkaEventType.UPDATE_PRODUCT.name())) {
            {

                Product existingProduct = repository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product with id " + id + " not found"));

                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setDescription(product.getDescription());

                repository.save(existingProduct);
            }
        }
    }
}