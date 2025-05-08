package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddress {
    String full_name;
    String phone_number;
    String address_line ;
    String city;
    String district;
    String ward;
    String postal_code;
}
