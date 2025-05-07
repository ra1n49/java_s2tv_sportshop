package com.s2tv.sportshop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpdateRequest {
    @JsonProperty("category_gender")
    CategoryGender categoryGender;

    @JsonProperty("category_type")
    String categoryType;

    @JsonProperty("category_parent_id")
    String categoryParentId;

    @JsonProperty("category_level")
    int categoryLevel = 1;
}
