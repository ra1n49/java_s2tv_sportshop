package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpdateResponse {
    CategoryGender category_gender;
    String category_type;
    String category_parent_id;
    int category_level;
}
