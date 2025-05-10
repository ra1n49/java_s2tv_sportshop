package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrder {

    @DBRef
    Product product_id;  // ref Product

    int quantity;

    String color;

    String variant;
}
