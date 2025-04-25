package com.s2tv.sportshop.models;

import jakarta.mail.Address;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Users")
public class Users {
    @Id
    private String id;

    private String userName;
    private String fullName;
    private String password;
    private String avtImg;
    private String email;
    private String phone;
    private List<Address> addresses;
    private Date birth;
    private String gender = "Nam";
    private String role = "user";
    private List<SearchHistory> searchHistories;
}



