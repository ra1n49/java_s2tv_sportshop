package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.enums.DiscountType;
import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.Order;
import com.s2tv.sportshop.repository.DiscountRepository;
import com.s2tv.sportshop.repository.OrderRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductRepository productRepository;



    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = orderMapper.toOrder(request);
        order = orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(String id, OrderStatus newStatus) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        existingOrder.setOrder_status(newStatus);
        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    private void validateDiscounts(List<String> discountIds) {
        if (discountIds == null || discountIds.isEmpty()) {
            return;
        }

        List<Discount> discounts = discountRepository.findAllById(discountIds);

        long shippingCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.SHIPPING )
                .count();

        long productCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.PRODUCT )
                .count();

        if (shippingCount > 1 || productCount > 1) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_COMBINATION);
        }
    }
}
