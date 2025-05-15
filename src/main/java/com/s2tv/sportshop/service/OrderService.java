package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.OrderProductRequest;
import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.enums.DiscountType;
import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderMapper orderMapper;

    public OrderResponse createOrder(String userId, OrderRequest request){
        System.out.println(request);
        if (request.getShippingAddress() == null) {
            throw new AppException(ErrorCode.SHIPPINGADDRESS_REQUIRE);
        }
        if (request.getOrderPaymentMethod() == null) {
            throw new AppException(ErrorCode.PAYMENTMETHOD_REQUIRE);
        }
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_REQUIRE);
        }

        double deliveryFee = 50000;
        double totalPrice = 0;
        List<OrderProduct> orderProducts = new ArrayList<>();
        Map<String, List<OrderProductRequest>> grouped = request.getProducts().stream()
                .collect(Collectors.groupingBy(OrderProductRequest::getProductId));

        for (Map.Entry<String, List<OrderProductRequest>> entry : grouped.entrySet()) {
            String productId = entry.getKey();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm: " + productId));

            int totalQuantity = 0;

            for (OrderProductRequest item : entry.getValue()) {
                Color color = product.getColors().stream()
                        .filter(c -> c.getColor_name().equals(item.getColorName()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND, "Không tìm thấy màu: " + item.getColorName()));

                Variant variant = color.getVariants().stream()
                        .filter(v -> v.getVariant_size().equals(item.getVariantName()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND, "Không tìm thấy size: " + item.getVariantName()));

                if (variant.getVariant_countInStock() < item.getQuantity()) {
                    throw new AppException(ErrorCode.OUT_OF_STOCK, "Sản phẩm " + productId + " đã hết hàng");
                }

                variant.setVariant_countInStock(variant.getVariant_countInStock() - item.getQuantity());
                totalQuantity += item.getQuantity();

                orderProducts.add(OrderProduct.builder()
                        .productId(product.getId())
                        .quantity(item.getQuantity())
                        .colorName(item.getColorName())
                        .variantName(item.getVariantName())
                        .price(variant.getVariant_price() * item.getQuantity())
                        .categoryId(product.getProduct_category())
                        .build());
            }

            product.setProduct_selled(product.getProduct_selled() + totalQuantity);
            product.setProduct_countInStock(product.getProduct_countInStock() - totalQuantity);
            productRepository.save(product);
        }
        totalPrice = orderProducts.stream().mapToDouble(OrderProduct::getPrice).sum();
        double totalDiscount = 0;

        List<String> discountIds = request.getDiscountIds();
        List<Discount> usedDiscounts = new ArrayList<>();
        if (discountIds != null && !discountIds.isEmpty()) {
            List<Discount> discounts = discountRepository.findAllById(discountIds);
            Date now = new Date();

            Discount appliedProductDiscount = null;
            Discount appliedShippingDiscount = null;

            for (Discount discount : discounts) {
                if (discount.getDiscountStartDay().after(now) || discount.getDiscountEndDay().before(now)) continue;
                if (totalPrice < discount.getMinOrderValue()) continue;

                if (discount.getDiscountType() == DiscountType.PRODUCT && appliedProductDiscount == null) {
                    totalDiscount += discount.getDiscountNumber() / 100.0 * totalPrice;
                    appliedProductDiscount = discount;
                } else if (discount.getDiscountType() == DiscountType.SHIPPING && appliedShippingDiscount == null) {
                    deliveryFee -= deliveryFee * discount.getDiscountNumber() / 100.0;
                    if (deliveryFee < 0) deliveryFee = 0;
                    appliedShippingDiscount = discount;
                }
            }

            if (appliedProductDiscount != null) usedDiscounts.add(appliedProductDiscount);
            if (appliedShippingDiscount != null) usedDiscounts.add(appliedShippingDiscount);

            for (Discount discount : usedDiscounts) {
                double current = discount.getDiscountAmount();
                if (current > 0) {
                    discount.setDiscountAmount(current - 1);
                    discountRepository.save(discount);
                }
            }
        }

        double totalFinal = totalPrice + deliveryFee - totalDiscount;
        int orderCode = new Random().nextInt(900000)+100000;
        LocalDate estimatedDate = LocalDate.now().plusDays(5);

        Order order = orderMapper.toOrder(request);
        order.setUserId((userId != null) ? userId : null);
        order.setDiscountIds(usedDiscounts.get);
        order.setProducts(orderProducts);
        order.setDeliveryFee((int) deliveryFee);
        order.setOrderTotalPrice(totalPrice);
        order.setOrderTotalDiscount(totalDiscount);
        order.setOrderTotalFinal(totalFinal);
        order.setOrderCode(orderCode);
        order.setOrderStatus(OrderStatus.CHO_XAC_NHAN);
        order.setEstimatedDeliveryDate(Date.from(estimatedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        order.setPaid(false);
        order.setFeedback(false);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(savedOrder);
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
//    @Transactional
//    public OrderResponse updateOrderStatus(String id, OrderStatus newStatus) {
//        Order existingOrder = orderRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
//
//        existingOrder.setOrder_status(newStatus);
//        Order updatedOrder = orderRepository.save(existingOrder);
//
//        return orderMapper.toOrderResponse(updatedOrder);
//    }

    /**
     * Kiểm tra sản phẩm có tồn tại đầy đủ không
     */
//    private void validateProductsExist(List<OrderProduct> products) {
//        if (products == null || products.isEmpty()) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        // Lấy danh sách product_id từ list OrderProduct
//        List<String> productIds = products.stream()
//                .map(OrderProduct::getProductId)
//                .toList();
//
//        // Kiểm tra có đủ sản phẩm trong DB không
//        long count = productRepository.countProductsByIdIn(productIds);
//        if (count != productIds.size()) {
//            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//    }

//    private void validateShippingAddressExists(String shippingAddressId) {
//        if (shippingAddressId == null || shippingAddressId.isBlank()) {
//            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND);
//        }
//
//        boolean exists = shippingAddressRepository.existsById(shippingAddressId);
//        if (!exists) {
//            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND);
//        }
//    }
}
