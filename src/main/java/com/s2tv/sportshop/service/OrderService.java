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
import com.s2tv.sportshop.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Tạo đơn hàng mới
     * - Kiểm tra tồn tại sản phẩm, địa chỉ giao hàng, mã giảm giá
     * - Lưu đơn hàng và trả về response
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Kiểm tra sản phẩm có tồn tại không
        validateProductsExist(request.getDiscount_ids());

        // Kiểm tra địa chỉ giao hàng có tồn tại không
        validateShippingAddressExists(request.getShipping_address().getId());


        // Kiểm tra mã giảm giá hợp lệ
        validateDiscounts(request.getDiscount_ids());

        // Map request thành entity và lưu vào DB
        Order order = orderMapper.toOrder(request);
        order = orderRepository.save(order);

        // Trả về response
        return orderMapper.toOrderResponse(order);
    }
    @Transactional
    public void deleteOrder(String id) {
        // Kiểm tra đơn hàng có tồn tại không
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Xóa đơn hàng
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
     * Lấy thông tin đơn hàng theo ID
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
        // Tìm đơn hàng
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật trạng thái mới
        existingOrder.setOrder_status(newStatus);
        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    /**
     * Kiểm tra mã giảm giá hợp lệ:
     * - Chỉ 1 mã giảm giá sản phẩm và 1 mã giảm giá vận chuyển
     * - Tất cả discountId phải tồn tại
     */
    private void validateDiscounts(List<String> discountIds) {
        if (discountIds == null || discountIds.isEmpty()) {
            return; // Không có discount thì bỏ qua
        }

        // Lấy danh sách discount từ DB
        List<Discount> discounts = discountRepository.findAllById(discountIds);

        // Nếu số lượng discount trả về khác số lượng id yêu cầu => có id không tồn tại
        if (discounts.size() != discountIds.size()) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_FOUND);
        }

        // Đếm số lượng discount theo loại
        long shippingCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.SHIPPING)
                .count();

        long productCount = discounts.stream()
                .filter(d -> d.getDiscountType() == DiscountType.PRODUCT)
                .count();

        // Nếu có hơn 1 mã mỗi loại => lỗi
        if (shippingCount > 1 || productCount > 1) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_COMBINATION);
        }
    }

    /**
     * Kiểm tra danh sách sản phẩm có tồn tại đầy đủ không
     */
    private void validateProductsExist(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Đếm số lượng sản phẩm có tồn tại
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
}
