package com.s2tv.sportshop.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
    private String name;
    private String phone;
    private String homeAddress;
    private String province;
    private String district;
    private String ward;
    private Boolean isDefault = false;
}
