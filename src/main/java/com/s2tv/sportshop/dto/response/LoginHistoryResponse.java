package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginHistoryResponse {
    private String id;
    private String userId;
    private Role role;
    private String ip;
    private String userAgent;
    private Date createdAt;
    private List<LoginActivityResponse> activities;
}
