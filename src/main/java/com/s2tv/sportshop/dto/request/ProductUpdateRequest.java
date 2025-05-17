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
    String productTitle;
    String productCategory;
    String productBrand;
    String productDescription;
    Boolean productDisplay;
    Boolean productFamous;
    double productRate;
    double productPercentDiscount;
}
