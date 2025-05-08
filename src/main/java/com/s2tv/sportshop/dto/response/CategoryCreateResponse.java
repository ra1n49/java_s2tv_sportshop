package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateResponse {
    CategoryGender category_gender;
    String category_type;
    String category_parent_id;
    int category_level;
}
