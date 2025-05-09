package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.model.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    String product_title;
    String product_category;
    String product_brand;
    String product_description;
    Boolean product_display;
    Boolean product_famous;
    double product_rate;
    double product_percent_discount;
}
