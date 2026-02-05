package com.spring.NexCart.service.impl;

import com.spring.NexCart.domain.Cart;
import com.spring.NexCart.domain.CartItem;
import com.spring.NexCart.domain.User;
import com.spring.NexCart.repository.CartItemRepository;
import com.spring.NexCart.repository.CartRepository;
import com.spring.NexCart.repository.UserRepository;
import com.spring.NexCart.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private  final CartItemRepository cartItemRepository;

    public UserServiceImpl(UserRepository userRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Cart> cartOpt = cartRepository.findByUserId(id);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            if (!items.isEmpty()) {
                throw new RuntimeException(
                        "Cannot delete user '" + user.getUsername() +
                                "' because they have " + items.size() +
                                " item(s) in their shopping cart. Please clear the cart first."
                );
            }

            cartItemRepository.deleteAll(items);
            cartRepository.delete(cart);
        }

        userRepository.deleteById(id);
    }
}