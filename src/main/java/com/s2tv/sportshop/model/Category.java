package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

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

    CategoryGender category_gender;
    String category_type;
    String category_parent_id; // id của category cha (nếu có)
    int category_level = 1;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
