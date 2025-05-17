package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.model.Category;
import com.s2tv.sportshop.model.Color;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateResponse {
    String productTitle;
    String productBrand;
    String productCategory;
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
}
