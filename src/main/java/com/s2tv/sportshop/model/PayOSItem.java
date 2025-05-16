package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayOSItem {
    String name;
    int quantity;
    int price;
}
