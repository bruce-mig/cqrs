package com.github.bruce_mig.cqrs.product_query_service.service;


import com.github.bruce_mig.cqrs.payload.ProductDto;
import com.github.bruce_mig.cqrs.payload.ProductEvent;

import java.util.List;

public interface ProductQueryService {

    List<ProductDto> getProducts();
}
