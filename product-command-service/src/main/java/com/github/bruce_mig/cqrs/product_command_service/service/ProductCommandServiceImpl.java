package com.github.bruce_mig.cqrs.product_command_service.service;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.payload.ProductEvent;
import com.github.bruce_mig.cqrs.product_command_service.entity.Product;
import com.github.bruce_mig.cqrs.product_command_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_command_service.util.KafkaEventType;
import com.github.bruce_mig.cqrs.product_command_service.util.ProductMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    @Value("${topic.name}")
    private String topicName;

    private final ProductRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductCommandServiceImpl(ProductRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        Product savedProduct = repository.save(product);

        ProductEvent event = ProductEvent.builder()
                .eventType(KafkaEventType.CREATE_PRODUCT.name())
                .productDto(productDto)
                .build();
        kafkaTemplate.send(topicName,event);

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

        productDto.setId(id);
        ProductEvent event = ProductEvent.builder()
                .eventType(KafkaEventType.UPDATE_PRODUCT.name())
                .productDto(productDto)
                .build();

        kafkaTemplate.send(topicName, event);

        return ProductMapper.toDto(updatedProduct);
    }

}
