package com.spring.NexCart.dto;

import com.spring.NexCart.domain.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private List<CartItem> items;
    private BigDecimal grandTotal;

    public CartResponse() {

    }

    public CartResponse(List<CartItem> items, BigDecimal grandTotal) {
        this.items = items;
        this.grandTotal = grandTotal;
    }
}
