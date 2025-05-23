package com.s2tv.sportshop.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.s2tv.sportshop.enums.OrderStatus;
import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    String id;
    int orderCode;
    String userId;
    OrderStatus orderStatus;
    List<Discount> discountIds;
    Address shippingAddress;
    List<OrderProduct> products;
    double orderTotalPrice;
    double orderTotalDiscount;
    double orderTotalFinal;
    int deliveryFee;
    PaymentMethod orderPaymentMethod;
    Date estimatedDeliveryDate;
    Date orderDeliveryDate;
    Date receivedDate;
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
    String checkoutUrl;
    Date createdAt;
}
