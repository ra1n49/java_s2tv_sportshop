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
    String product_title;
    String product_brand;
    String product_category;
    String product_description;
    String product_img;
    double product_price;
    double product_percent_discount;
    List<Color> colors;
    boolean product_display;
    int product_countInStock;
    boolean product_famous;
    double product_rate;
    int product_selled;
}
