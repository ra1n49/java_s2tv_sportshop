package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.LoginAction;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginActivity {
    private LoginAction action;
    private String orderId;
    private String prevStatus;
    private String newStatus;
    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
