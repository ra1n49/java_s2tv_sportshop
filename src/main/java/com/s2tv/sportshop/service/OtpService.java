package com.s2tv.sportshop.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> expiryStorage = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        otpStorage.put(email, otp);
        expiryStorage.put(email, LocalDateTime.now().plusMinutes(5));

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) return false;
        if (expiryStorage.get(email).isBefore(LocalDateTime.now())) return false;

        return otpStorage.get(email).equals(otp);
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
        expiryStorage.remove(email);
    }
}
