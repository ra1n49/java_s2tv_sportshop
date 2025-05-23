package com.s2tv.sportshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean authenticated;
    private String accessToken;
    private String refreshToken;
    private UserInfoResponse user;
}
