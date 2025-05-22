package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.ResetPasswordRequest;
import com.s2tv.sportshop.dto.request.UserCreateRequest;
import com.s2tv.sportshop.dto.response.AuthResponse;
import com.s2tv.sportshop.dto.response.UserInfoResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.UserMapper;
import com.s2tv.sportshop.model.User;
import com.s2tv.sportshop.repository.UserRepository;
import com.s2tv.sportshop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public UserResponse createUser(UserCreateRequest UserCreateData){

        if (userRepository.existsByUsername(UserCreateData.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(UserCreateData);
        user.setPassword(passwordEncoder.encode(UserCreateData.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return AuthResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserInfoResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }

    public void sendOtp(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
    }

    public void verifyOtp(String email, String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (!isValid) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }

    public void resetPassword(ResetPasswordRequest req) {
        verifyOtp(req.getEmail(), req.getOtp());

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        otpService.clearOtp(req.getEmail());
    }

    public Map<String, String> refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.isValidToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NON_EXISTED));

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}
