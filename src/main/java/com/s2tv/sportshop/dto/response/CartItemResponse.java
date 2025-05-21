package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.s2tv.sportshop.model.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private String productId;
    private String colorName;
    private String variantName;
    private int quantity;
    private Product product;
}
