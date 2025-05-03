package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
    