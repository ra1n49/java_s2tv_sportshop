package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Tạo đơn hàng
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ApiResponse<>(0, "Success", response);
    }

    // Lấy tất cả đơn hàng
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return new ApiResponse<>(0, "Success", orders);
    }

    // Lấy đơn hàng theo ID
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String id) {
        OrderResponse order = orderService.getOrderById(id);
        return new ApiResponse<>(0, "Success", order);
    }

    // Cập nhật đơn hàng
    @PutMapping("/{id}")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable String id, @RequestBody OrderRequest request) {
        OrderResponse updatedOrder = orderService.updateOrder(id, request);
        return new ApiResponse<>(0, "Success", updatedOrder);
    }

    // Xóa đơn hàng
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return new ApiResponse<>(0, "Success", "Xóa đơn hàng thành công");
    }

    // Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable String id, @RequestParam String newStatus) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return new ApiResponse<>(0, "Cập nhật trạng thái thành công", updatedOrder);
    }

}
