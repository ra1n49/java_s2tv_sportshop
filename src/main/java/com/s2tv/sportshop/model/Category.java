package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.CategoryGender;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "Category")
public class Category {

    @Id
    private String id;

    private CategoryGender category_gender;

    private String category_type;

    private String category_parent_id; // id của category cha (nếu có)

    private int category_level = 1;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
