package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "LoginHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {
    @Id
    private String id;

    private String userId;

    private Role role;

    private String ip;

    private String userAgent;

    private List<LoginActivity> activities;
    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
