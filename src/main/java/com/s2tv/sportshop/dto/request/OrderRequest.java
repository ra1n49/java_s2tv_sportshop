package com.s2tv.sportshop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    List<String> discountIds;
    int deliveryFee;
    String paymentMethod;
    List<OrderProductRequest> products;
}
