package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "Order")
public class Order {

    @Id
    String id;

    @DBRef
    List<Discount> discount_ids;

    @DBRef
    User user_id;

    int delivery_fee;

    ShippingAddress shipping_address;

    List<OrderProduct> products;


    OrderStatus order_status; // Enum: Chờ xác nhận, Đang chuẩn bị hàng, ...

    boolean is_require_refund;  // yêu cầu hoàn hàng

    PaymentMethod order_payment_method; // Enum: Credit_card, Paypal, Cod, Apple_pay, Momo

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

    @Field("order_code")
    Integer orderCode;   // mã đơn hàng

    Integer order_loyalty;   // điểm tích lũy từ đơn hàng

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
