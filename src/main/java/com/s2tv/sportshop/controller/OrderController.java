package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.OrderService;
import lombok.RequiredArgsConstructor;
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
        String userId = (userPrincipal != null) ? userPrincipal.getUser().getId() : null;
        return ApiResponse.<OrderResponse>builder()
                .EC(0)
                .EM("Tạo đơn hàng mới thành công")
                .result(orderService.createOrder(userId, request))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable("id") String id) {
        orderService.deleteOrder(id);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa đơn hàng thành công")
                .build();
    }

    @GetMapping("/get-details/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable("id") String id) {
        return ApiResponse.<OrderResponse>builder()
                .EC(0)
                .EM("Lấy chi tiết đơn hàng thành công")
                .result(orderService.getOrderById(id))
                .build();
    }

    @GetMapping("/get-all")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách đơn hàng thành công")
                .result(orderService.getAllOrders())
                .build();
    }
}
