package com.s2tv.sportshop.service;

import com.s2tv.sportshop.model.PayOSItem;
import com.s2tv.sportshop.dto.request.CreatePaymentRequest;
import com.s2tv.sportshop.dto.request.OrderProductRequest;
import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.enums.DiscountType;
import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.enums.Role;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.type.CheckoutResponseData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final OrderMapper orderMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(String userId, OrderRequest request){
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
                if (discount.getDiscountAmount() > 0) {
                    discount.setDiscountAmount(discount.getDiscountAmount() - 1);
                    discountRepository.save(discount);
                }
            }
        }

        double totalFinal = totalPrice + deliveryFee - totalDiscount;
        Long orderCode = new Random().nextLong(900000)+100000;
        LocalDate estimatedDate = LocalDate.now().plusDays(5);

        Order order = orderMapper.toOrder(request);
        order.setUserId(userId);
        order.setDiscountIds(usedDiscounts);
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

        CheckoutResponseData resultPayOS = null;

        if (order.getOrderPaymentMethod() == PaymentMethod.PAYPAL) {
            String description = "Thanh toán đơn " + order.getOrderCode();

            List<PayOSItem> items = savedOrder.getProducts().stream()
                    .map(op -> PayOSItem.builder()
                            .name(op.getVariantName() + " - " + op.getColorName())
                            .quantity(op.getQuantity())
                            .price((int) op.getPrice())
                            .build())
                    .toList();

            CreatePaymentRequest paymentRequest = CreatePaymentRequest.builder()
                    .orderCode(savedOrder.getOrderCode())
                    .amount(2000)
                    .description(description)
                    .products(items)
                    .orderId(savedOrder.getId())
                    .build();

            try {
                resultPayOS = paymentService.createPayment(paymentRequest);
            } catch (Exception e) {
                throw new AppException(ErrorCode.CREATE_PAYMENT_FAILED);
            }
            savedOrder.setCheckoutUrl(resultPayOS.getCheckoutUrl());
        }

        if (savedOrder.getUserId() != null) {
            cartService.clearCartByUserId(savedOrder.getUserId());
        }

        return orderMapper.toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrder(String orderStatus) {
        List<Order> orders;

        if (!"all".equalsIgnoreCase(orderStatus)) {
            OrderStatus statusEnum;
            try {
                statusEnum = OrderStatus.valueOf(orderStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
            }

            orders = orderRepository.findByOrderStatus(statusEnum);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse getDetailOrder(String id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if(order.getUserId() != null) {
            if(!order.getUserId().equals(user.getId()) && !Role.ADMIN.equals(user.getRole())) {
                throw new AppException(ErrorCode.FORBIDDEN_ORDER_ACCESS);
            }
        }

        return orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> getOrderByUser(String userId, String orderStatus) {
        if(userId == null || userId.isBlank()) {
            throw new AppException(ErrorCode.USER_ID_IS_REQUIRED);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<Order> orders;

        if(!"all".equalsIgnoreCase(orderStatus)) {
            OrderStatus statusEnum;
            try {
                statusEnum = OrderStatus.valueOf(orderStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
            }
            orders = orderRepository.findByUserIdAndOrderStatus(userId, statusEnum);
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse updateStatus(
            String orderId,
            String orderStatus,
            String currentUserId,
            Role currentUserRole) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(orderStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        boolean isOwner = order.getUserId().equals(currentUserId);
        boolean isAdmin = Role.ADMIN.equals(currentUserRole);
        OrderStatus currentStatus = order.getOrderStatus();

        if(!canUpdateOrderStatus(currentStatus, newStatus, isOwner, isAdmin)) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_ORDER_STATUS);
        }

        order.setOrderStatus(newStatus);

        if(isAdmin && newStatus == OrderStatus.HOAN_THANH) {
            order.setPaid(true);
            order.setReceivedDate(new Date());
        }

        if(isOwner && newStatus == OrderStatus.YEU_CAU_HOAN) {
            order.setRequireRefund(true);
        }

        if(newStatus == OrderStatus.HUY_HANG || newStatus == OrderStatus.HOAN_HANG) {
            restoreInventory(order.getProducts());
        }

        orderRepository.save(order);

        sendOrderStatusNotification(order, newStatus, currentStatus, currentUserRole);

        return orderMapper.toOrderResponse(order);
    }

    private boolean canUpdateOrderStatus(OrderStatus currentStatus, OrderStatus newStatus, boolean isOwner, boolean isAdmin) {
        List<OrderStatus> disallowedTargets = List.of(
                OrderStatus.YEU_CAU_HOAN,
                OrderStatus.HOAN_HANG,
                OrderStatus.HUY_HANG
        );

        if(isAdmin) {
            if(currentStatus == OrderStatus.HOAN_THANH ||
                    List.of(OrderStatus.HUY_HANG,
                            OrderStatus.HOAN_HANG).contains(currentStatus)) {
                return false;
            }

            if(List.of(OrderStatus.CHO_XAC_NHAN,
                    OrderStatus.DANG_CHUAN_BI_HANG,
                    OrderStatus.DANG_GIAO).contains(currentStatus)) {
                return !disallowedTargets.contains(newStatus);
            }

            if(currentStatus == OrderStatus.YEU_CAU_HOAN &&
                    List.of(OrderStatus.HOAN_HANG, OrderStatus.HOAN_THANH).contains(newStatus)) {
                return true;
            }
            return false;
        }

        if(isOwner && currentStatus == OrderStatus.CHO_XAC_NHAN && newStatus == OrderStatus.HUY_HANG) {
            return true;
        }

        if(isOwner && currentStatus == OrderStatus.HOAN_THANH && newStatus == OrderStatus.YEU_CAU_HOAN) {
            return true;
        }

        if(isOwner && currentStatus == OrderStatus.YEU_CAU_HOAN && newStatus == OrderStatus.HOAN_THANH) {
            return true;
        }

        return false;
    }

    private void restoreInventory(List<OrderProduct> products) {
        for(OrderProduct p : products) {
            Product product = productRepository.findById(p.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Color color = product.getColors().stream()
                    .filter(c -> c.getColor_name().equals(p.getColorName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

            Variant variant = color.getVariants().stream()
                    .filter(v -> v.getVariant_size().equals(p.getVariantName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

            variant.setVariant_countInStock(variant.getVariant_countInStock() + p.getQuantity());
            product.setProduct_countInStock(product.getProduct_countInStock() + p.getQuantity());

            productRepository.save(product);
        }
    }

    private void sendOrderStatusNotification(Order order, OrderStatus newStatus, OrderStatus oldStatus, Role role) {
        Map<Role, Map<OrderStatus, String>> descMap = Map.of(
                Role.ADMIN, Map.of(
                        OrderStatus.DANG_CHUAN_BI_HANG, "Đơn hàng của bạn đang được chuẩn bị",
                        OrderStatus.DANG_GIAO, "Đơn hàng đang được giao đến bạn",
                        OrderStatus.HOAN_THANH, oldStatus == OrderStatus.YEU_CAU_HOAN
                                ? "Rất tiếc, yêu cầu hoàn hàng của bạn không được chấp nhận"
                                : "Đơn hàng đã được giao, cảm ơn bạn đã mua hàng tại WTM Sport!",
                        OrderStatus.HOAN_HANG, "Đơn hàng đã được hoàn trả thành công"
                ),
                Role.USER, Map.of(
                        OrderStatus.YEU_CAU_HOAN, "Yêu cầu hoàn hàng của bạn đã được tiếp nhận, vui lòng chờ để được liên hệ làm việc",
                        OrderStatus.HUY_HANG, "Đơn hàng của bạn đã được hủy, xin lỗi quý khách"
                )
        );

        Map<Role, Map<OrderStatus, String>> imageMap = Map.of(
                Role.ADMIN, Map.of(
                        OrderStatus.DANG_CHUAN_BI_HANG, "/images/status/prepare.jpg",
                        OrderStatus.DANG_GIAO, "/images/shipping.png",
                        OrderStatus.HOAN_THANH, oldStatus == OrderStatus.YEU_CAU_HOAN
                                ? "/images/return-rejected.png"
                                : "/images/delivered-success.png",
                        OrderStatus.HOAN_HANG, "/images/return-success.jpg"
                ),
                Role.USER, Map.of(
                        OrderStatus.YEU_CAU_HOAN, "/images/user-request-cancelled.jpg",
                        OrderStatus.HUY_HANG, "/images/user-cancel.png"
                )
        );

        String desc = descMap.getOrDefault(role, Map.of()).get(newStatus);
        String img = imageMap.getOrDefault(role, Map.of()).get(newStatus);

//        if (desc != null && img != null) {
//            notificationService.createNotification(
//                    order.getUserId(),
//                    "Tình trạng đơn hàng",
//                    "Đơn hàng #" + order.getId() + " đã được cập nhật.",
//                    desc,
//                    order.getId(),
//                    img
//            );
//        }
    }

    public void handleCancelPayment(Long orderCode, String userId, Role role) {
        if(orderCode == null) {
            throw new AppException(ErrorCode.ORDERCODE_IS_REQUIRED);
        }

        if(!paymentService.checkPaymentIsCancel(orderCode)) {
            throw new AppException(ErrorCode.CANCEL_CONDITION_FAILED);
        }

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if(order.getOrderPaymentMethod() == PaymentMethod.PAYPAL && !order.isPaid()) {

        }
    }
}
