package com.s2tv.sportshop.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
