package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.Address;
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
    Address shippingAddress;
    List<OrderProductRequest> products;
    PaymentMethod orderPaymentMethod;
    String orderNote;
    String email;
}
