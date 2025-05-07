package com.s2tv.sportshop.dto.request;

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
    List<String> discountIds;
    int deliveryFee;
    String paymentMethod;
    Date estimatedDeliveryDate;
    Date initialDeliveryDate;
}
