package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.model.Color;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductGetDetailsResponse {
    String id;

    String productTitle;
    String productBrand;
    Category productCategory;
    String productDescription;
    String productImg;
    double productPrice;
    double productPercentDiscount;
    List<Color> colors;
    boolean productDisplay;
    int productCountInStock;
    boolean productFamous;
    double productRate;
    int productSelled;

    Date createdAt;
    Date updatedAt;
}
