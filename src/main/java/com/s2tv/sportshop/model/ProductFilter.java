package com.s2tv.sportshop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductFilter {
    private String category;

    @JsonProperty("category_gender")
    private String categoryGender;

    @JsonProperty("category_sub")
    private String categorySub;

    @JsonProperty("price_min")
    private Integer priceMin;

    @JsonProperty("price_max")
    private Integer priceMax;

    @JsonProperty("product_color")
    private String productColor;

    @JsonProperty("product_brand")
    private String productBrand;
}
