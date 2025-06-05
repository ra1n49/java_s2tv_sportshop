package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.NotificationRequest;
import com.s2tv.sportshop.dto.response.MonthlyRevenueResponse;
import com.s2tv.sportshop.dto.response.RevenueResponse;
import com.s2tv.sportshop.enums.*;
import com.s2tv.sportshop.model.PayOSItem;
import com.s2tv.sportshop.dto.request.CreatePaymentRequest;
import com.s2tv.sportshop.dto.request.OrderProductRequest;
import com.s2tv.sportshop.dto.request.OrderRequest;
import com.s2tv.sportshop.dto.response.OrderResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.OrderMapper;
import com.s2tv.sportshop.model.*;
import com.s2tv.sportshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.type.CheckoutResponseData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final OrderMapper orderMapper;

    private final PaymentService paymentService;

    private final CartService cartService;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final MongoTemplate mongoTemplate;
    private final EmailService emailService;

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
                        .filter(c -> c.getColorName().equals(item.getColorName()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND, "Không tìm thấy màu: " + item.getColorName()));

                Variant variant = color.getVariants().stream()
                        .filter(v -> v.getVariantSize().equals(item.getVariantName()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND, "Không tìm thấy size: " + item.getVariantName()));

                if (variant.getVariantCountInStock() < item.getQuantity()) {
                    throw new AppException(ErrorCode.OUT_OF_STOCK, "Sản phẩm " + productId + " đã hết hàng");
                }

                variant.setVariantCountInStock(variant.getVariantCountInStock() - item.getQuantity());
                totalQuantity += item.getQuantity();

                orderProducts.add(OrderProduct.builder()
                        .productId(product.getId())
                        .quantity(item.getQuantity())
                        .colorName(item.getColorName())
                        .variantName(item.getVariantName())
                        .price(variant.getVariantPrice() * item.getQuantity())
                        .categoryId(product.getProductCategory())
                        .build());
            }

            product.setProductSelled(product.getProductSelled() + totalQuantity);
            product.setProductCountInStock(product.getProductCountInStock() - totalQuantity);
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

        String email;
        if (request.getEmail() != null) {
            email = request.getEmail();
        } else if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));
            email = user.getEmail();
        } else {
            throw new AppException(ErrorCode.EMAIL_REQUIRE);
        }
        order.setEmail(email);

        Order savedOrder = orderRepository.save(order);

        CheckoutResponseData resultPayOS = null;

        if (order.getOrderPaymentMethod() == PaymentMethod.PAYPAL) {
            String description = "Thanh toán đơn " + order.getOrderCode();

            List<PayOSItem> items = savedOrder.getProducts().stream()
                    .map(op -> {

                        Product product = productRepository.findById(op.getProductId())
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                        String itemName = product.getProductTitle() + " - " +
                                op.getColorName() + " - Size " +
                                op.getVariantName();

                        return PayOSItem.builder()
                                .name(itemName)
                                .quantity(op.getQuantity())
                                .price((int) op.getPrice())
                                .build();
                    })
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

        emailService.sendOrderConfirmationEmail(email, order.getOrderCode(), order.getOrderTotalFinal());
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
            if (user == null || (!order.getUserId().equals(String.valueOf(user.getId()))
                    && !Role.ADMIN.equals(user.getRole()))) {
                throw new AppException(ErrorCode.FORBIDDEN_ORDER_ACCESS);
            }
        }

        for (OrderProduct product : order.getProducts()) {
            Product pr = productRepository.findById(product.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            product.setProduct(pr);
        }

        OrderResponse response = orderMapper.toOrderResponse(order);

        if (user != null) {
            response.setEmail(user.getEmail());
        } else {
            response.setEmail(order.getEmail());
        }

        return response;
    }

    public List<OrderResponse> getOrderByUser(String userId, String orderStatus) {
        if (userId == null || userId.isBlank()) {
            throw new AppException(ErrorCode.USER_ID_IS_REQUIRED);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        List<Order> orders;

        if (!"all".equalsIgnoreCase(orderStatus)) {
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

        Set<String> productIds = orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .map(OrderProduct::getProductId)
                .collect(Collectors.toSet());

        Map<String, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (Order order : orders) {
            for (OrderProduct op : order.getProducts()) {
                Product product = productMap.get(op.getProductId());
                if (product == null) {
                    throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                }
                op.setProduct(product);
            }
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

        boolean isOwner = order.getUserId() != null && order.getUserId().equals(currentUserId);
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
        emailService.sendOrderStatusUpdateEmail(order.getEmail(), order.getOrderCode() , orderStatus);

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
                    .filter(c -> c.getColorName().equals(p.getColorName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

            Variant variant = color.getVariants().stream()
                    .filter(v -> v.getVariantSize().equals(p.getVariantName()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

            variant.setVariantCountInStock(variant.getVariantCountInStock() + p.getQuantity());
            product.setProductCountInStock(product.getProductCountInStock() + p.getQuantity());

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
                        OrderStatus.DANG_CHUAN_BI_HANG, "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742987/products/1747742985401-prepare.jpg.jpg",
                        OrderStatus.DANG_GIAO, "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742984/products/1747742983746-shipping.png.png",
                        OrderStatus.HOAN_THANH, oldStatus == OrderStatus.YEU_CAU_HOAN
                                ? "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742983/products/1747742982031-return-rejected.png.png"
                                : "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742978/products/1747742977475-delivered-success.png.png",
                        OrderStatus.HOAN_HANG, "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742980/products/1747742978898-return-success.jpg.jpg"
                ),
                Role.USER, Map.of(
                        OrderStatus.YEU_CAU_HOAN, "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742989/products/1747742987962-user-request-cancelled.jpg.jpg",
                        OrderStatus.HUY_HANG, "https://res.cloudinary.com/dved7vhdn/image/upload/v1747742976/products/1747742974362-user-cancel.png.png"
                )
        );

        String desc = descMap.getOrDefault(role, Map.of()).get(newStatus);
        String img = imageMap.getOrDefault(role, Map.of()).get(newStatus);

        if(desc != null && img != null) {
            NotificationRequest request = NotificationRequest.builder()
                    .orderId(order.getId())
                    .notifyType(NotifyType.DON_HANG)
                    .notifyTitle("Đơn hàng #" + order.getId() + " đã được cập nhật")
                    .notifyDescription(desc)
                    .imageUrl(img)
                    .build();
            notificationService.createNotification(order.getUserId(), request);
        }
    }

    public OrderResponse handleCancelPayment(Long orderCode, String userId, Role role) {
        if(orderCode == null) {
            throw new AppException(ErrorCode.ORDERCODE_IS_REQUIRED);
        }

        if(!paymentService.checkPaymentIsCancel(orderCode)) {
            throw new AppException(ErrorCode.CANCEL_CONDITION_FAILED);
        }

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if(order.getOrderPaymentMethod() == PaymentMethod.PAYPAL && !order.isPaid()) {
            return updateStatus(order.getId(), OrderStatus.HUY_HANG.name(), userId, role);
        } else {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }
    }

    public RevenueResponse getRevenue(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("createdAt").gte(startDate).lt(endDate)),
                Aggregation.project()
                        .andExpression("month(createdAt)").as("month")
                        .and("orderStatus").as("status")
                        .and("isPaid").as("payment")
                        .and("orderTotalFinal").as("total"),
                Aggregation.group("month", "status", "payment")
                        .sum("total").as("total"),
                Aggregation.project()
                        .and("_id.month").as("month")
                        .and("_id.status").as("status")
                        .and("_id.payment").as("payment")
                        .and("total").as("total")
        );

        List<Document> documents = mongoTemplate.aggregate(aggregation, "Order", Document.class).getMappedResults();

        List<MonthlyRevenueResponse> monthly = new ArrayList<>();

        for(int i = 1; i <= 12; i++) {
            int month = i;
            double completed = 0;
            double cancelled = 0;
            double paid = 0;

            for (Document doc : documents) {
                int m = doc.getInteger("month");
                if (m != month) continue;

                String status = doc.getString("status");
                Boolean isPaid = doc.getBoolean("payment", false);
                Double total = doc.getDouble("total");

                if ("HOAN_THANH".equals(status)) completed += total;
                if ("HUY_HANG".equals(status)) cancelled += total;
                if (Boolean.TRUE.equals(isPaid)) paid += total;
            }

            monthly.add(new MonthlyRevenueResponse(month, completed, cancelled, paid));
        }

        return new RevenueResponse(monthly);
    }
}
