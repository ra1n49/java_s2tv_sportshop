package com.s2tv.sportshop.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "Product")
public class Product {
    @Id
    String id;

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

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
