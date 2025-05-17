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
    CategoryGender categoryGender;

    String categoryType;

    String categoryParentId;

    Integer categoryLevel;
}
