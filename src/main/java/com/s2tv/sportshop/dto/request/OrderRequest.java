package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.ShippingAddress;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.s2tv.sportshop.model.ProductOrder;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    List<ProductOrder> products;
    List<String> discountIds;
    int deliveryFee;
    ShippingAddress shippingAddress;
    PaymentMethod paymentMethod;
    Date orderDeliveryDate;
    Date estimatedDeliveryDate;
    String orderNote;
}
