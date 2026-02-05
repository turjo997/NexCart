package com.spring.NexCart.service.impl;

import com.spring.NexCart.domain.Cart;
import com.spring.NexCart.domain.CartItem;
import com.spring.NexCart.domain.Product;
import com.spring.NexCart.domain.User;
import com.spring.NexCart.dto.CartDto;
import com.spring.NexCart.repository.CartItemRepository;
import com.spring.NexCart.repository.CartRepository;
import com.spring.NexCart.service.CartService;
import com.spring.NexCart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Please enter a quantity greater than 0.");
        }

        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        if (!product.getActive()) {
            throw new RuntimeException("This product is currently unavailable.");
        }

        Cart cart = getOrCreateCart(user);

        // Find existing item in cart
        int currentQuantityInCart = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .map(CartItem::getQuantity)
                .orElse(0);

        int totalRequested = currentQuantityInCart + quantity;

        if (totalRequested > product.getStockQuantity()) {
            String msg = String.format(
                    "Sorry, only %d item(s) left in stock. " +
                            "You already have %d in your cart, and requested %d more.",
                    product.getStockQuantity(),
                    currentQuantityInCart,
                    quantity
            );
            throw new RuntimeException(msg);
        }

        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProduct(product);
                            newItem.setQuantity(quantity);
                            cart.addItem(newItem);
                        }
                );

        return cartRepository.save(cart);
    }

    public Cart removeFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(item -> cartItemRepository.delete(item));
        return cart;
    }

    public Cart updateQuantity(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(user, productId);
        }

        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not in cart"));

        Product product = item.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return cart;
    }

    public CartDto getCartDto(User user) {
        Cart cart = getOrCreateCart(user);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        BigDecimal grandTotal = items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto(items, grandTotal);
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cart.getId()));
    }
}
