package com.s2tv.sportshop.service;

import com.s2tv.sportshop.dto.request.LoginActivityRequest;
import com.s2tv.sportshop.dto.request.LoginHistoryRequest;
import com.s2tv.sportshop.dto.response.LoginActivityResponse;
import com.s2tv.sportshop.dto.response.LoginHistoryResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.exception.ErrorCode;
import com.s2tv.sportshop.mapper.LoginHistoryMapper;
import com.s2tv.sportshop.model.LoginActivity;
import com.s2tv.sportshop.model.LoginHistory;
import com.s2tv.sportshop.repository.LoginHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginHistoryService {
    LoginHistoryRepository loginHistoryRepository;
    LoginHistoryMapper loginHistoryMapper;

    public LoginHistoryResponse createLoginHistory(LoginHistoryRequest loginHistoryRequest) {
        LoginHistory loginHistory = LoginHistory.builder()
                .userId(loginHistoryRequest.getUserId())
                .ip(loginHistoryRequest.getIp())
                .role(loginHistoryRequest.getRole())
                .userAgent(loginHistoryRequest.getUserAgent())
                .activities(new ArrayList<>())
                .build();

        return loginHistoryMapper.toLoginHistoryResponse(loginHistoryRepository.save(loginHistory));
    }

    public LoginHistoryResponse updateLoginHistory(String loginHistoryId, LoginActivityRequest loginActivityRequest) {
        LoginHistory loginHistory = loginHistoryRepository.findById(loginHistoryId)
                .orElseThrow(() -> new AppException(ErrorCode.LOGIN_HISTORY_NOTFOUND));

        LoginActivity loginActivity = loginHistoryMapper.toLoginActivity(loginActivityRequest);
        loginActivity.setCreatedAt(new Date());

        loginHistory.getActivities().add(loginActivity);

        return loginHistoryMapper.toLoginHistoryResponse(loginHistoryRepository.save(loginHistory));
    }

    public List<LoginHistoryResponse> getAllLoginHistory(){
        List<LoginHistory> loginHistories = loginHistoryRepository.findAllByOrderByCreatedAtDesc();

        return loginHistoryMapper.toLoginHistoriesResponse(loginHistories);
    }

    public LoginHistoryResponse getDetailLoginHistory(String loginHistoryId){
        LoginHistory loginHistory = loginHistoryRepository.findById(loginHistoryId)
                .orElseThrow(() -> new AppException(ErrorCode.LOGIN_HISTORY_NOTFOUND) );

        return loginHistoryMapper.toLoginHistoryResponse(loginHistory);
    }

}
