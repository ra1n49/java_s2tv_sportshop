package com.s2tv.sportshop.dto.response;


import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.*;
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
    int orderCode;
    OrderStatus orderStatus;
    List<String> discountIds;
    Address shippingAddress;
    List<OrderProduct> products;
    double orderTotalPrice;
    double orderTotalDiscount;
    double orderTotalFinal;
    int deliveryFee;
    PaymentMethod orderPaymentMethod;
    Date estimatedDeliveryDate;
    Date orderDeliveryDate;
    String orderNote;
    boolean isPaid;
    String checkoutUrl;
    Date createdAt;
}
