package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateResponse {
    CategoryGender categoryGender;
    String categoryType;
    String categoryParentId;
    Integer categoryLevel;
}
