package com.s2tv.sportshop.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private String productId;
    private String colorName;
    private String variantName;
    private int quantity;
}
