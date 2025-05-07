package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddress {
    String fullName;
    String phoneNumber;
    String addressLine;
    String city;
    String district;
    String ward;
    String postalCode;
}
