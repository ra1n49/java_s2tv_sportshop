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
    List<String> discount_ids;
    int delivery_fee;
    ShippingAddress shipping_address;
    PaymentMethod payment_method;
    Date order_delivery_date;
    Date estimated_delivery_date;
    String order_note;
}
