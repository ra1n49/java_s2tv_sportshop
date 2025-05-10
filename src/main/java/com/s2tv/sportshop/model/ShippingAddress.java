package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddress {
    String name;
    String phone;
    String home_address;
    String province;
    String district;
    String ward;
}
