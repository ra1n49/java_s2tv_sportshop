package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.Gender;
import com.s2tv.sportshop.enums.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "User")
public class User {
    @Id
    private String id;

    private String username;
    private String fullname;
    private String password;
    private String avtimg;
    private String email;
    private String phone;
    private List<Address> addresses;
    private Date birth;
    private Gender gender = Gender.Nam;
    private Role role;
    private List<SearchHistory> searchhistory;
    private List<String> discounts;

    @CreatedDate
    @Field("created_at")
    Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    Date updatedAt;
}
