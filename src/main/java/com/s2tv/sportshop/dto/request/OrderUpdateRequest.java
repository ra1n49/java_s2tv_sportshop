package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.Product;
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
public class OrderUpdateRequest {

    ShippingAddress shippingAddress;

    List<Product> products;

    List<String> discountIds;

    PaymentMethod paymentMethod;

    OrderStatus orderStatus;

    Boolean isRequireRefund;

    Date estimatedDeliveryDate;

    String orderNote;

    Boolean isFeedback;

    Boolean isPaid;

    Date orderDeliveryDate;
}
