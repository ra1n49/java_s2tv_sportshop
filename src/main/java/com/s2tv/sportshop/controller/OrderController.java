package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.request.OrderStatusUpdateRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.RevenueResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody OrderRequest request) {
        System.out.println(userPrincipal);
        String userId = (userPrincipal != null) ? userPrincipal.getUser().getId() : null;
        return ApiResponse.<OrderResponse>builder()
                .EC(0)
                .EM("Tạo đơn hàng mới thành công")
                .result(orderService.createOrder(userId, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all")
    public ApiResponse<List<OrderResponse>> getAllOrder(@RequestParam(defaultValue = "all") String orderStatus) {
        return ApiResponse.<List<OrderResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách đơn hàng thành công")
                .result(orderService.getAllOrder(orderStatus))
                .build();
    }

    @GetMapping("/get-detail/{id}")
    public ApiResponse<OrderResponse> getDetailOrder(@PathVariable("id") String id,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = (userPrincipal != null) ? userPrincipal.getUser() : null;
        return ApiResponse.<OrderResponse>builder()
                .EC(0)
                .EM("Xem chi tiết đơn hàng thành công")
                .result(orderService.getDetailOrder(id, user))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-by-user")
    public ApiResponse<List<OrderResponse>> getOrderByUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @RequestParam(defaultValue = "all") String orderStatus) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<List<OrderResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách đơn hàng thành công")
                .result(orderService.getOrderByUser(userId, orderStatus))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update-status/{id}")
    public ApiResponse<OrderResponse> updateStatus(@PathVariable("id") String orderId,
                                                   @RequestBody OrderStatusUpdateRequest request,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        return ApiResponse.<OrderResponse>builder()
                .EC(0)
                .EM("Cập nhật trạng thái đơn hàng thành công")
                .result(orderService.updateStatus(orderId, request.getStatus(), user.getId(), user.getRole()))
                .build();
    }

    @PutMapping("/handle-cancel-payment/{orderCode}")
    public ApiResponse<OrderResponse > handleCancelPayment(
            @PathVariable Long orderCode,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userPrincipal.getUser();
        return ApiResponse.<OrderResponse >builder()
                .EC(0)
                .EM("Hủy đơn hàng thành công")
                .result(orderService.handleCancelPayment(orderCode, user.getId(), user.getRole()))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-revenue")
    public ApiResponse<RevenueResponse> getRevenue(@RequestParam int year) {
        return ApiResponse.<RevenueResponse>builder()
                .EC(0)
                .EM("Lấy thống kê thành công")
                .result(orderService.getRevenue(year))
                .build();
    }
}
