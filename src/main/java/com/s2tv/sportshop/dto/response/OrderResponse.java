package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.model.ShippingAddress;
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
    ShippingAddress shippingAddress;
    String orderStatus;
    Date orderDate;
    Date orderDeliveryDate;
    Date estimatedDeliveryDate;
    String paymentMethod;
    double orderTotalPrice;
    double orderFinalPrice;
    double orderTotalDiscount;
    String orderNote;
    boolean isFeedback;
    Date createdAt;
    Date updatedAt;
}
