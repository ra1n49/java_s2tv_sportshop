package com.s2tv.sportshop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductGetAllRequest {
    List<String> category;
    List<String> category_gender;
    List<String> category_sub;
    Double price_min;
    Double price_max;
    List<String> product_color;
    List<String> product_brand;
}
