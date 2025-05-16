package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.CartResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{userId}/items")
    public ApiResponse<CartResponse> addItemToCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody CartItemRequest cartItem) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addItemToCart(userId, cartItem))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{userId}/items")
    public ApiResponse<CartResponse> updateItemQuantity(@PathVariable String userId,
                                                       @RequestParam String productId,
                                                        @RequestParam int quantity) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItemQuantity(userId, productId, quantity))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping("/{userId}/items/{productId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable String userId,
                                                @PathVariable String productId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItemFromCart(userId, productId))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCart(@PathVariable String userId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(userId))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteCart(@PathVariable String userId) {
        cartService.deleteCart(userId);
        return ApiResponse.<Void>builder().result(null).build();
    }
}
