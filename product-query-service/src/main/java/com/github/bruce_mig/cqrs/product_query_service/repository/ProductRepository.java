package com.github.bruce_mig.cqrs.product_query_service.repository;


import com.github.bruce_mig.cqrs.product_query_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
