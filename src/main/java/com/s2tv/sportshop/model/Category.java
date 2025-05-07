package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "Category")
public class Category {
    @Id
    String id;

    @Field("category_gender")
    CategoryGender categoryGender;

    @Field("category_type")
    String categoryType;

    @Field("category_parent_id")
    String categoryParentId;

    @Field("category_level")
    int categoryLevel = 1;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
