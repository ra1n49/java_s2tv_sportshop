package com.s2tv.sportshop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    List<Discount> discountIds;
    String userId;
    int deliveryFee;
    Address shippingAddress;
    List<OrderProduct> products;
    OrderStatus orderStatus;
    PaymentMethod orderPaymentMethod;
    Date orderDeliveryDate;
    Date estimatedDeliveryDate;
    double orderTotalPrice;
    double orderTotalFinal;
    double orderTotalDiscount;
    String checkoutUrl;
    String orderNote;
    String email;

    @Field("isPaid")
    @JsonProperty("isPaid")
    boolean paid;

    @Field("isFeedback")
    @JsonProperty("isFeedback")
    boolean feedback;

    @Field("isRequireRefund")
    @JsonProperty("isRequireRefund")
    boolean requireRefund;

    Date receivedDate;
    Long orderCode;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
