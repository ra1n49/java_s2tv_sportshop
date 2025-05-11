package com.s2tv.sportshop.dto.response;


import com.s2tv.sportshop.model.Discount;
import com.s2tv.sportshop.model.OrderProduct;
import com.s2tv.sportshop.model.ShippingAddress;
import com.s2tv.sportshop.model.User;
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

    List<Discount> discount_ids;

    User user_id;

    int delivery_fee;

    ShippingAddress shipping_address;

    List<OrderProduct> products;

    String order_status;

    boolean is_require_refund;

    String order_payment_method;

    Date order_delivery_date;

    Date estimated_delivery_date;

    double order_total_price;

    double order_total_final;

    double order_total_discount;

    String checkoutUrl;

    String order_note;

    boolean is_feedback;

    boolean is_paid;

    Date received_date;

    Integer order_code;

    Integer order_loyalty;

    Date createdAt;

    Date updatedAt;
}
