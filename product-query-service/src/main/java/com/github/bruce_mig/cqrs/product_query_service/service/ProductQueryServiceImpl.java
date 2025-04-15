package com.github.bruce_mig.cqrs.product_query_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.payload.ProductEvent;
import com.github.bruce_mig.cqrs.product_query_service.entity.Product;
import com.github.bruce_mig.cqrs.product_query_service.repository.ProductRepository;
import com.github.bruce_mig.cqrs.product_query_service.util.KafkaEventType;
import com.github.bruce_mig.cqrs.product_query_service.util.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000),
            exclude = {NullPointerException.class, JsonProcessingException.class}
    )
    @KafkaListener(topics = "${topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void processProductEvents(ProductEvent productEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Consuming the json message: {}  from topic: {}", productEvent.toString(), topic);
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

    @DltHandler
    public void listenDLT(ProductEvent productEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset){
        log.info("Dead Letter Topic received: {}, from: {}, offset: {}",productEvent.toString(), topic, offset);

    }
}