package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.Order;
import com.s2tv.sportshop.model.OrderProduct;
import com.s2tv.sportshop.repository.OrderRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import com.s2tv.sportshop.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Tạo đơn hàng mới
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Kiểm tra sản phẩm có tồn tại không
        validateProductsExist(request.getProducts());

        // Kiểm tra địa chỉ giao hàng có tồn tại không
        validateShippingAddressExists(request.getShipping_address().getId());

        // Không cần validateDiscounts() nữa - đã làm trong mapper
        // Ngày giao hàng không được trước ngày hôm nay, ngày dự kiến phải sau ngày giao.
        validateDeliveryDate(request.getOrder_delivery_date(), request.getEstimated_delivery_date());

        // Tổng tiền phải trả phải bằng tổng tiền - tiền giảm giá + tiền vận chuyển
        validateTotalPrice(request);

        // Map request thành entity và lưu vào DB
        Order order = orderMapper.toOrder(request);
        order = orderRepository.save(order);

        // Trả về response
        return orderMapper.toOrderResponse(order);
    }

    /**
     * Xóa đơn hàng
     */
    @Transactional
    public void deleteOrder(String id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        orderRepository.delete(existingOrder);
    }

    /**
     * Lấy danh sách tất cả đơn hàng
     */
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Transactional
    public OrderResponse updateOrderStatus(String id, OrderStatus newStatus) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        validateOrderStatusChangeAllowed(existingOrder.getOrder_status(), newStatus);
        existingOrder.setOrder_status(newStatus);
        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    /**
     * Kiểm tra sản phẩm có tồn tại đầy đủ không
     */
    private void validateProductsExist(List<OrderProduct> products) {
        if (products == null || products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Lấy danh sách product_id từ list OrderProduct
        List<String> productIds = products.stream()
                .map(OrderProduct::getProduct_id)   // ✅ Đúng: lấy field product_id trong OrderProduct
                .toList();

        // Kiểm tra có đủ sản phẩm trong DB không
        long count = productRepository.countProductsByIdIn(productIds);
        if (count != productIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    /**
     * Kiểm tra địa chỉ giao hàng có tồn tại không
     */
    private void validateShippingAddressExists(String shippingAddressId) {
        if (shippingAddressId == null || shippingAddressId.isBlank()) {
            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND);
        }

        boolean exists = shippingAddressRepository.existsById(shippingAddressId);
        if (!exists) {
            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND);
        }
    }
    private void validateDeliveryDate(Date orderDeliveryDate, Date estimatedDeliveryDate) {
        Date today = new Date();

        if (orderDeliveryDate.before(today)) {
            throw new AppException(ErrorCode.INVALID_DELIVERY_DATE);
        }

        if (estimatedDeliveryDate.before(orderDeliveryDate)) {
            throw new AppException(ErrorCode.INVALID_ESTIMATED_DELIVERY_DATE);
        }
    }
    /**
     * Kiểm tra tổng tiền cuối cùng hợp lệ
     */
    private void validateTotalPrice(OrderRequest request) {
        double expectedFinalTotal = request.getOrder_total_price()
                - request.getOrder_total_discount()
                + request.getDelivery_fee();

        if (request.getOrder_total_final() != expectedFinalTotal) {
            throw new AppException(ErrorCode.INVALID_TOTAL_PRICE);
        }
    }
    private void validateOrderStatusChangeAllowed(OrderStatus currentStatus, OrderStatus newStatus) {
        // Đơn đã HUỶ hoặc ĐÃ_GIAO → không được đổi trạng thái nữa
        if (currentStatus == OrderStatus.HUY_HANG || currentStatus == OrderStatus.HOAN_THANH) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_CHANGE);
        }

        // Không được chuyển lùi trạng thái
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_CHANGE);
        }
    }

}
