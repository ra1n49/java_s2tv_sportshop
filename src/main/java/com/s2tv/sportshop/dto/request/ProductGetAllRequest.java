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
    List<String> categoryGender;
    List<String> categorySub;
    Double priceMin;
    Double priceMax;
    List<String> productColor;
    List<String> productBrand;
}
