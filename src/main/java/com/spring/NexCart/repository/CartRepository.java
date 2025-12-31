package com.spring.NexCart.repository;

import com.spring.NexCart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndActiveTrue(Long userId);
}
