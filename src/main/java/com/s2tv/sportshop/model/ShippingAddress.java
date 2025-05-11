package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "ShippingAddress")   // Thêm nếu chưa có
public class ShippingAddress {
    @Id
    String id;   // Thêm dòng này để có id

    String name;
    String phone;
    String home_address;
    String province;
    String district;
    String ward;
}
