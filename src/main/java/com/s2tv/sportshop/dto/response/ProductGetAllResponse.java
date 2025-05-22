package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.model.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductGetAllResponse {
    int total;
    List<ProductGetDetailsResponse> products;
}
