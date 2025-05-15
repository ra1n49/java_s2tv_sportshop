package com.s2tv.sportshop.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private String userId;
    private List<CartItemResponse> cartItems;
}
