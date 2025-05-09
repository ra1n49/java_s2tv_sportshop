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
    List<String> discount_ids;
    int delivery_fee;
    ShippingAddress shipping_address;
    String order_status;
    Date order_date;
    Date order_delivery_date;
    Date estimated_delivery_date;
    String payment_method;
    double order_total_price;
    double order_final_price;
    double order_total_discount;
    String order_note;
    boolean is_feedback;
    Date createdAt;
    Date updatedAt;
}
