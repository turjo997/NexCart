package com.spring.NexCart.service;

import com.spring.NexCart.domain.Cart;
import com.spring.NexCart.domain.User;
import com.spring.NexCart.dto.CartDto;

public interface CartService {

    Cart getOrCreateCart(User user);

    Cart addToCart(User user, Long productId, int quantity);

    Cart removeFromCart(User user, Long productId);

    Cart updateQuantity(User user, Long productId, int quantity);

    CartDto getCartDto(User user);

    void clearCart(User user);

}
