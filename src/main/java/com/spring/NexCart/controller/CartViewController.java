package com.spring.NexCart.controller;

import com.spring.NexCart.domain.User;
import com.spring.NexCart.dto.CartDto;
import com.spring.NexCart.service.CartService;
import com.spring.NexCart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartViewController {

    private final CartService cartService;
    private final UserService userService;

    public CartViewController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/view")
    public String viewCart(Model model, @RequestParam(required = false) Long userId) {
        // Use the userId from URL, fallback to 1 for testing
        Long activeUserId = (userId != null) ? userId : 1L;

        User user = userService.findById(activeUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartDto cartDto = cartService.getCartDto(user);

        model.addAttribute("cartItems", cartDto.items());
        model.addAttribute("grandTotal", cartDto.grandTotal());

        // Optional: pass user info to view if you want to display "Cart for John Doe"
        model.addAttribute("currentUser", user);

        return "cart/view";
    }


    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false) Long userId,
            RedirectAttributes redirectAttributes) {

        Long activeUserId = (userId != null) ? userId : 1L;

        try {
            User user = userService.findById(activeUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            cartService.updateQuantity(user, productId, quantity);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cart updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage() != null ? e.getMessage() : "Could not update cart. Please try again.");
        }

        return "redirect:/cart/view?userId=" + activeUserId;
    }

    @PostMapping("/remove")
    public String removeItem(
            @RequestParam Long productId,
            @RequestParam(required = false) Long userId,
            RedirectAttributes redirectAttributes) {

        Long activeUserId = (userId != null) ? userId : 1L;

        try {
            User user = userService.findById(activeUserId).orElseThrow();
            cartService.removeFromCart(user, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not remove item.");
        }

        return "redirect:/cart/view?userId=" + activeUserId;
    }

    @PostMapping("/clear")
    public String clearCart(
            @RequestParam(required = false) Long userId,
            RedirectAttributes redirectAttributes) {

        Long activeUserId = (userId != null) ? userId : 1L;

        try {
            User user = userService.findById(activeUserId).orElseThrow();
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not clear cart.");
        }

        return "redirect:/cart/view?userId=" + activeUserId;
    }
}