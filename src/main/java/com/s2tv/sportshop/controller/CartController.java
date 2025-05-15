package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.model.Cart;
import com.s2tv.sportshop.model.CartItem;
import com.s2tv.sportshop.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/")
    public ApiResponse<Cart> addItemToCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody CartItem cartItem) {
        String userId = userPrincipal.getUser().getId();
        Cart cart = cartService.addItemToCart(userId, cartItem);
        return ApiResponse.<Cart>builder()
                .EC(0)
                .EM("Thêm sản phẩm vào giỏ thành công")
                .result(cart)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{productId}")
    public ApiResponse<Cart> deleteItemFromCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String productId) {
        String userId = userPrincipal.getUser().getId();

        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update-quantity")
    public ApiResponse<Cart> updateItemQuantity(@RequestParam String userId, @RequestParam String productId, @RequestParam int quantity) {
        Cart cart = cartService.updateItemQuantity(userId, productId, quantity);
        return ApiResponse.<Cart>builder()
                .result(cart)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public ApiResponse<Cart> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        Cart cart = cartService.getCart(userId);
        return ApiResponse.<Cart>builder()
                .EC(0)
                .EM("Lấy danh sách giỏ hàng thành công")
                .result(cart)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/")
    public ApiResponse<String> deleteCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        cartService.deleteCart(userId);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Xóa giỏ hàng thành công")
                .build();
    }

}
