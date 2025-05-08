package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.DiscountStatus;
import com.s2tv.sportshop.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private String id;
    private String discountTitle;
    private String discountCode;
    private DiscountType discountType;
    private Date discountStartDay;
    private Date discountEndDay;
    private double discountAmount;
    private int discountNumber;
    private List<String> applicableProducts;
    private List<String> applicableCategories;
    private double minOrderValue = 0.0;
    private String description = "";
    private DiscountStatus status = DiscountStatus.ACTIVE;

}
