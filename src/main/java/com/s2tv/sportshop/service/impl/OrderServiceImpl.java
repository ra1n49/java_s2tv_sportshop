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
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // Validate sản phẩm không rỗng
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        // Validate deliveryFee không âm
        if (request.getDeliveryFee() < 0) {
            throw new AppException(ErrorCode.INVALID_DELIVERY_FEE);
        }

        // Mapping từ OrderRequest → Order entity
        Order order = orderMapper.toOrder(request);

        // Tính tổng giá đơn hàng (giả lập: mỗi sp quantity * 100k)
        double totalPrice = request.getProducts().stream()
                .mapToDouble(p -> p.getQuantity() * 100_000)
                .sum();

        order.setOrderTotalPrice(totalPrice);
        order.setOrderFinalPrice(totalPrice + request.getDeliveryFee());

        // Lưu DB
        Order savedOrder = orderRepository.save(order);

        // Mapping từ Order entity → OrderResponse
        return orderMapper.toOrderResponse(savedOrder);
    }
    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }
    @Override
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse updateOrder(String id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật lại thông tin từ request
        Order updatedOrder = orderMapper.toOrder(request);
        updatedOrder.setId(id); // giữ nguyên id

        // Tính lại tổng giá
        double totalPrice = request.getProducts().stream()
                .mapToDouble(p -> p.getQuantity() * 100_000)
                .sum();

        updatedOrder.setOrderTotalPrice(totalPrice);
        updatedOrder.setOrderFinalPrice(totalPrice + request.getDeliveryFee());

        Order savedOrder = orderRepository.save(updatedOrder);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }

}
