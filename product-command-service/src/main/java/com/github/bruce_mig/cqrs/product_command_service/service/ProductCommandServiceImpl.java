package com.github.bruce_mig.cqrs.product_command_service.service;

import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.payload.ProductEvent;
import com.github.bruce_mig.cqrs.product_command_service.entity.Product;
import com.github.bruce_mig.cqrs.product_command_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_command_service.util.KafkaEventType;
import com.github.bruce_mig.cqrs.product_command_service.util.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService {

    @Value("${topic.name}")
    private String topicName;

    private final ProductRepository repository;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductCommandServiceImpl(ProductRepository repository, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        product.setId(null);
        Product savedProduct = repository.save(product);

        ProductEvent event = ProductEvent.newBuilder()
                .setEventType(KafkaEventType.CREATE_PRODUCT.name())
                .setProductDto(productDto)
                .build();

        String kafkaKey = UUID.randomUUID().toString();

        Message<ProductEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, kafkaKey)
                .build();

        CompletableFuture<SendResult<String, ProductEvent>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] to topic=[{}] with offset=[{}]", event,topicName, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to : {}", event, ex.getMessage());
            }
        });

        return ProductMapper.toDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + id + " not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());

        Product updatedProduct = repository.save(existingProduct);

        productDto.setId(id);
        ProductEvent event = ProductEvent.newBuilder()
                .setEventType(KafkaEventType.UPDATE_PRODUCT.name())
                .setProductDto(productDto)
                .build();

        String kafkaKey = UUID.randomUUID().toString();

        Message<ProductEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, kafkaKey)
                .build();

        CompletableFuture<SendResult<String, ProductEvent>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] to topic=[{}] with offset=[{}]", event,topicName, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to : {}", event, ex.getMessage());
            }
        });

        return ProductMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteProduct(Long id) {
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + id + " not found"));

        repository.delete(existingProduct);

        ProductEvent event = ProductEvent.newBuilder()
                .setEventType(KafkaEventType.DELETE_PRODUCT.name())
                .setProductDto(ProductMapper.toDto(existingProduct))
                .build();

        String kafkaKey = UUID.randomUUID().toString();

        Message<ProductEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, kafkaKey)
                .build();

        CompletableFuture<SendResult<String, ProductEvent>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] to topic=[{}] with offset=[{}]", event,topicName, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to : {}", event, ex.getMessage());
            }
        });

    }

}
