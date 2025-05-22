package com.s2tv.sportshop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DecreaseCartItemRequest {
    String productId;
    String colorName;
    String variantName;
}
