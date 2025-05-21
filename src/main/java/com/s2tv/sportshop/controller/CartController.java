package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.CartItemRequest;
import com.s2tv.sportshop.dto.request.DecreaseCartItemRequest;
import com.s2tv.sportshop.dto.request.RemoveCartItemRequest;
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
    @PostMapping
    public ApiResponse<CartResponse> addItemToCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestBody CartItemRequest cartItem) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<CartResponse>builder()
                .EC(0)
                .EM("Cập nhật giỏ hàng thành công")
                .result(cartService.addItemToCart(userId, cartItem))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/decrease-quantity")
    public ApiResponse<CartResponse> updateItemQuantity(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @RequestBody DecreaseCartItemRequest request) {

        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<CartResponse>builder()
                .EC(0)
                .EM("Giảm số lượng sản phẩm thành công")
                .result(cartService.updateItemQuantity(userId,
                        request.getProductId(),
                        request.getColorName(),
                        request.getVariantName()))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping("/item")
    public ApiResponse<CartResponse> removeItem(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @RequestBody RemoveCartItemRequest request) {

        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<CartResponse>builder()
                .EC(0)
                .EM("Xóa sản phẩm khỏi giỏ hàng thành công")
                .result(cartService.removeItemFromCart(userId,
                        request.getProductId(),
                        request.getColorName(),
                        request.getVariantName()))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping
    public ApiResponse<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<CartResponse>builder()
                .EC(0)
                .EM("Lấy giỏ hàng thành công")
                .result(cartService.getCart(userId))
                .build();
    }

    @PreAuthorize("isAuthenticated")
    @DeleteMapping
    public ApiResponse<Void> deleteCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();

        cartService.deleteCart(userId);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa toàn bộ giỏ hàng thành công")
                .build();
    }
}
