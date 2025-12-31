package com.spring.NexCart.controller;

import com.spring.NexCart.domain.User;
import com.spring.NexCart.dto.CartDto;
import com.spring.NexCart.service.CartService;
import com.spring.NexCart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final UserService userService; // assume you have this

    @GetMapping("/{userId}")
    public CartDto getCart(@PathVariable Long userId) {
        User user = userService.findById(userId).orElseThrow();
        return cartService.getCartDto(user);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {

        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = (Integer) request.get("quantity");

        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            cartService.addToCart(user, productId, quantity);

            CartDto cartDto = cartService.getCartDto(user);
            return ResponseEntity.ok(cartDto);

        } catch (RuntimeException e) {
            // Return error message with 400 status
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}