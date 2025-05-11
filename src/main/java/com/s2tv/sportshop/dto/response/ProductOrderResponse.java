package com.s2tv.sportshop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderResponse {
    String product_id;
    int quantity;
    String color;
    String variant;
}
