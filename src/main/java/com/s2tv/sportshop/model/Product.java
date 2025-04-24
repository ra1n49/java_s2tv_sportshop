package com.s2tv.sportshop.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "Product")
public class Product {
    @Id
    private String id;

    private String product_title;
    private String product_brand;
//    @DBRef
//    private Category product_category;
    private String product_description;
    private String product_img;
    private double product_price;
    private int product_percent_discount;
    private List<Color> colors;
    private boolean product_display;
    private int product_countInStock;
    private boolean product_famous;
    private double product_rate;
//    @DBRef
//    private List<Feedback> product_feedbacks;
    private int product_selled;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
