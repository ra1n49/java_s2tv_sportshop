package com.s2tv.sportshop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BotSuggestResponse {
    private String message;
    private List<ProductShortResponse> products;
}

