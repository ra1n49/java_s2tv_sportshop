package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.PaymentMethod;
import com.s2tv.sportshop.model.Product;
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
public class OrderCreateRequest {


    String userId;

    List<String> discountIds;


    ShippingAddress shippingAddress;


    List<Product> products;


    PaymentMethod paymentMethod;

    String orderNote;

    Date estimatedDeliveryDate;
}
