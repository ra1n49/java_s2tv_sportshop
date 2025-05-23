package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistoryRequest {
    private String userId;
    private Role role;
    private String ip;
    private String userAgent;
}
