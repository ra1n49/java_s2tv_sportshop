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
    List<String> discountIds;
    String userId;
    int deliveryFee;
    Address shippingAddress;
    List<OrderProduct> products;
    OrderStatus orderStatus;
    boolean isRequireRefund;
    PaymentMethod orderPaymentMethod;
    Date orderDeliveryDate;
    Date estimatedDeliveryDate;
    double orderTotalPrice;
    double orderTotalFinal;
    double orderTotalDiscount;
    String checkoutUrl;
    String orderNote;
    boolean isFeedback;
    boolean isPaid;
    Date receivedDate;
    int orderCode;

    @CreatedDate
    @Field("created_at")
    Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    Date updatedAt;
}
