package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.model.PayOSItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    Long orderCode;
    int amount = 2000;
    String description;
    List<PayOSItem> products;
    String orderId;
}
