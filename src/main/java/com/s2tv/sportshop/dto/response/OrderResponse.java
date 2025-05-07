package com.s2tv.sportshop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    List<ProductOrder> products;
    List<String> discountIds;
    int deliveryFee;
    String orderStatus;
    Date orderDate;
    Date estimatedDeliveryDate;
    Date initialDeliveryDate;
    String paymentMethod;
    double orderTotalPrice;
    double orderFinalPrice;
    boolean isFeedback;
    Date createdAt;
    Date updatedAt;
}
