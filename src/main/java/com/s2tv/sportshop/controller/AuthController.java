package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.AuthRequest;
import com.s2tv.sportshop.dto.request.ResetPasswordRequest;
import com.s2tv.sportshop.dto.request.UserCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.AuthResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.repository.UserRepository;
import com.s2tv.sportshop.service.AuthService;

import com.s2tv.sportshop.service.EmailService;
import com.s2tv.sportshop.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/sign-up")
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest userCreationRequest){
        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Đăng ký thành công")
                .result(authService.createUser(userCreationRequest))
                .build();
    }

    @PostMapping("/sign-in")
    public ApiResponse<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest.getUsername(), authRequest.getPassword());

        return ApiResponse.<AuthResponse>builder()
                .EC(0)
                .EM("Đăng nhập thành công")
                .result(authResponse)
                .build();
    }

    @PostMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestBody ResetPasswordRequest req) {
        authService.sendOtp(req.getEmail());
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("OTP đã được gửi đến email")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody ResetPasswordRequest req) {
        authService.verifyOtp(req.getEmail(), req.getOtp());
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("OTP hợp lệ")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Mật khẩu đã được đặt lại thành công")
                .build();
    }


}

