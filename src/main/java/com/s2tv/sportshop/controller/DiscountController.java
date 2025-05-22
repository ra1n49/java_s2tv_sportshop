package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.DiscountCreateRequest;
import com.s2tv.sportshop.dto.request.DiscountUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<DiscountResponse> createDiscount(@RequestBody DiscountCreateRequest discountCreateRequest) {
        return ApiResponse.<DiscountResponse>builder()
                .EC(0)
                .EM("Tạo mã giảm giá thành công")
                .result(discountService.createDiscount(discountCreateRequest))
                .build();
    }

    @GetMapping("/get-detail/{id}")
    public ApiResponse<DiscountResponse> getDetailDiscount(@PathVariable String id) {
        return ApiResponse.<DiscountResponse>builder()
                .EC(0)
                .EM("Lấy chi tiết mã giảm giá thành công")
                .result(discountService.getDetailDiscount(id))
                .build();
    }

    @GetMapping("/get-all")
    public ApiResponse<List<DiscountResponse>> getAllDiscount() {
        return ApiResponse.<List<DiscountResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách mã giảm giá thành công")
                .result(discountService.getAllDiscount())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ApiResponse<DiscountResponse> updateDiscount(@RequestBody DiscountUpdateRequest discountUpdateRequest, @PathVariable String id){
        return ApiResponse.<DiscountResponse>builder()
                .EC(0)
                .EM("Cập nhật mã giảm giá thành công")
                .result(discountService.updateDiscount(discountUpdateRequest, id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteDiscount(@PathVariable String id){
        discountService.deleteDiscount(id);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Xóa mã giảm giá thành công")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/get-for-order")
    public ApiResponse<List<DiscountResponse>> getDiscountForOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody List<String> productIds) {
        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<List<DiscountResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách mã giảm giá cho đơn hàng thành công")
                .result(discountService.getDiscountForOrder(userId, productIds))
                .build();
    }
}
