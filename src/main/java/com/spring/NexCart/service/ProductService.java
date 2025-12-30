package com.spring.NexCart.service;

import com.spring.NexCart.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(Product product);

    List<Product> findAll();

    List<Product> findActiveProducts();

    Optional<Product> findById(Long id);

    void deleteById(Long id);
}
