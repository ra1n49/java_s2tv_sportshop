package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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
    int deliveryFee;
    List<String> productIds; // Danh sách id sản phẩm
    String orderStatus;
    Date orderDate;
    Date estimatedDeliveryDate;
    Date initialDeliveryDate;
    String paymentMethod;
    double orderTotalPrice;
    double orderFinalPrice;
    boolean isFeedback;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
