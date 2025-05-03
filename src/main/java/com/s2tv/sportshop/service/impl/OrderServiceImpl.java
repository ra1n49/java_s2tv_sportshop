package com.s2tv.sportshop.service.impl;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.Order;
import com.s2tv.sportshop.repository.OrderRepository;
import com.s2tv.sportshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        Order order = orderMapper.toOrder(request);

        // Tổng giá (giả lập: quantity * 100k cho đơn giản — bạn có thể tính kỹ hơn)
        double totalPrice = request.getProducts().stream()
                .mapToDouble(p -> p.getQuantity() * 100_000)
                .sum();

        order.setOrderTotalPrice(totalPrice);
        order.setOrderFinalPrice(totalPrice + request.getDeliveryFee());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }
}
