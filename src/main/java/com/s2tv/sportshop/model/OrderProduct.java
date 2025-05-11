package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

// Embedded product info trong đơn hàng — không phải full Product
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProduct {
    String product_id;
    int quantity;
    String size;
    double price;
    String product_title;
}
