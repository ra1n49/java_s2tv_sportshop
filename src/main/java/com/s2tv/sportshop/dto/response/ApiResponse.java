package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"EC", "EM", "result"})
public class ApiResponse<T> {
    @JsonProperty("EC")
    int EC = 0;

    @JsonProperty("EM")
    String EM;
    T result;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductOrder {

        String productId;  // ID của sản phẩm
        int quantity;      // Số lượng sản phẩm trong đơn hàng
    }
}
