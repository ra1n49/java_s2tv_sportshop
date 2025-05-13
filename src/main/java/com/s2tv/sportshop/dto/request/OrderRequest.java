package com.s2tv.sportshop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s2tv.sportshop.model.OrderProduct;
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
public class OrderRequest {
    List<String> discount_ids; // danh sách discountId (string)

    String user_id; // userId (string)

    int delivery_fee;

    ShippingAddress shipping_address;
    List<OrderProduct> products;

    String order_status; // optional khi tạo mới (default là "CHO_XAC_NHAN")

    boolean is_require_refund;

    String order_payment_method; // COD / MOMO...

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

    @JsonProperty("order_code")
    Integer orderCode;

    Integer order_loyalty;
}
