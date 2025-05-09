package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
}
