package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.*;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.AuthResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.service.AuthService;

import com.s2tv.sportshop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

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
                .EM("Mã OTP đã được gửi đến email")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody ResetPasswordRequest req) {
        authService.verifyOtp(req.getEmail(), req.getOtp());
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Mã OTP hợp lệ")
                .build();
    }

    @PatchMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Mật khẩu đã được đặt lại thành công")
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, String> tokens = authService.refreshToken(refreshToken);

        return ApiResponse.<Map<String, String>>builder()
                .EC(0)
                .EM("Làm mới token thành công")
                .result(tokens)
                .build();
    }

    @PostMapping("/signup-with-google")
    public ApiResponse<AuthResponse > signUpWithGoogle(@RequestBody GoogleSignUpRequest request) {
        return ApiResponse.<AuthResponse >builder()
                .EC(0)
                .EM("Đăng ký Google thành công")
                .result(authService.signUpWithGoogle(request))
                .build();
    }

    @PostMapping("signin-with-google")
    public ApiResponse<AuthResponse > loginWithGoogle(@RequestBody GoogleSignInRequest request) {
        return ApiResponse.<AuthResponse >builder()
                .EC(0)
                .EM("Đăng nhập Google thành công")
                .result(authService.loginWithGoogle(request))
                .build();
    }
}

