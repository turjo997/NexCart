package com.spring.NexCart.dto;

import com.spring.NexCart.domain.CartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        List<CartItem> items,
        BigDecimal grandTotal
) {}