package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.s2tv.sportshop.enums.LoginAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginActivityResponse {
    private LoginAction action;
    private String orderId;
    private String prevStatus;
    private String newStatus;
    private Date createdAt;
}
