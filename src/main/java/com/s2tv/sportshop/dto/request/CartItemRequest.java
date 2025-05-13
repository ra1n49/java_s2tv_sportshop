package com.s2tv.sportshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {
    //@NotBlank(message = "productId không được để trống")
    private String productId;

//    @NotBlank(message = "colorName không được để trống")
    private String colorName;

//    @NotBlank(message = "variantName không được để trống")
    private String variantName;

//    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}
