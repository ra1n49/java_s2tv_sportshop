package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.CartResponse;
import com.s2tv.sportshop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Validated
public class CartController {

    CartService cartService;

    @PostMapping("/{userId}/create")
    public ApiResponse<CartResponse> createCart(@PathVariable String userId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.createCart(userId))
                .build();
    }

    @PostMapping("/{userId}/items")
    public ApiResponse<CartResponse> addItemToCart(@PathVariable String userId, @RequestBody CartItemRequest cartItem) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addItemToCart(userId, cartItem))
                .build();
    }

    @PatchMapping("/{userId}/items")
    public ApiResponse<CartResponse> updateItemQuantity(@PathVariable String userId,
                                                        @RequestParam String productId,
                                                        @RequestParam int quantity) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItemQuantity(userId, productId, quantity))
                .build();
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable String userId,
                                                @PathVariable String productId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItemFromCart(userId, productId))
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCart(@PathVariable String userId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(userId))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteCart(@PathVariable String userId) {
        cartService.deleteCart(userId);
        return ApiResponse.<Void>builder().result(null).build();
    }
}
