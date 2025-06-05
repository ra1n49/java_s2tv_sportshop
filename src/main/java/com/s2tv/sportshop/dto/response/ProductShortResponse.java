package com.s2tv.sportshop.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductShortResponse {
    private String id;
    private String productTitle;
    private String productImg;
    private Double productPrice;
}
