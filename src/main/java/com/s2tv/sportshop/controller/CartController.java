package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.model.Cart;
import com.s2tv.sportshop.model.CartItem;
import com.s2tv.sportshop.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/create")
    public ApiResponse<Cart> createCart(@RequestParam String userId) {
        Cart cart = cartService.createCart(userId);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @PostMapping("/add-to-cart")
    public ApiResponse<Cart> addItemToCart(@RequestParam String userId, @RequestBody CartItem cartItem) {
        Cart cart = cartService.addItemToCart(userId, cartItem);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @PostMapping("/remove-item")
    public ApiResponse<Cart> deleteItemFromCart(@RequestParam String userId, @RequestParam String productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @PatchMapping("/update-quantity")
    public ApiResponse<Cart> updateItemQuantity(@RequestParam String userId, @RequestParam String productId, @RequestParam int quantity) {
        Cart cart = cartService.updateItemQuantity(userId, productId, quantity);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @GetMapping("/view")
    public ApiResponse<Cart> getCart(@RequestParam String userId) {
        Cart cart = cartService.getCart(userId);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteCart(@RequestParam String userId) {
        cartService.deleteCart(userId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

}
