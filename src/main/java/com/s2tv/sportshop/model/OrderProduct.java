package com.s2tv.sportshop.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProduct {
    String productId;
    int quantity;
    String colorName;
    String variantName;
    double price;
    String categoryId;
    Product product;
}
