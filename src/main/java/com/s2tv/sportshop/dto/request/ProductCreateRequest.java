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
    String productTitle;
    String productCategory;
    String productBrand;
    String productDescription;
    boolean productDisplay = true;
    boolean productFamous = false;
    double productRate = 0.0;
    double productPercentDiscount = 0.0;
}
