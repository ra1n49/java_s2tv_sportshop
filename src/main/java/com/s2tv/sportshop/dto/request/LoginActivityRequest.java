package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.LoginAction;
import com.s2tv.sportshop.enums.Role;
import lombok.Data;

@Data
public class LoginActivityRequest {
    private LoginAction action;
    private String orderId;
    private String prevStatus;
    private String newStatus;

}
