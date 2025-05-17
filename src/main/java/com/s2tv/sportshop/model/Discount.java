package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.DiscountStatus;
import com.s2tv.sportshop.enums.DiscountType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Discount")
public class Discount {
    @Id
    private String id;

    private String discountTitle;
    private String discountCode;
    private DiscountType discountType;
    private Date discountStartDay;
    private Date discountEndDay;
    private int discountAmount;
    private int discountNumber;
    private List<String> applicableProducts;
    private List<String> applicableCategories;
    private double minOrderValue = 0.0;
    private String description = "";
    private DiscountStatus status = DiscountStatus.ACTIVE;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
