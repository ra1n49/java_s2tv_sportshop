package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.model.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    String product_title;
    String product_category;
    String product_brand;
    String product_description;
    boolean product_display = true;
    boolean product_famous = false;
    double product_rate = 0.0;
    double product_percent_discount = 0.0;
}
