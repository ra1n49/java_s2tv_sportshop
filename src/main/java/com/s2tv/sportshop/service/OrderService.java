package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    // Tạo đơn hàng
    OrderResponse createOrder(OrderRequest request);

    // Lấy tất cả đơn hàng
    List<OrderResponse> getAllOrders();

    // Lấy chi tiết đơn hàng theo id
    OrderResponse getOrderById(String id);

    // Cập nhật đơn hàng
    OrderResponse updateOrder(String id, OrderRequest request);

    // Xoá đơn hàng
    void deleteOrder(String id);
}
