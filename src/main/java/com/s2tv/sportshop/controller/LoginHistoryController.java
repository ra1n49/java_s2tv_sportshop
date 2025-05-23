package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.LoginActivityRequest;
import com.s2tv.sportshop.dto.request.LoginHistoryRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.LoginHistoryResponse;
import com.s2tv.sportshop.service.LoginHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login-history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginHistoryController {
    LoginHistoryService loginHistoryService;

    @PostMapping("/create")
    ApiResponse<LoginHistoryResponse> createLoginHistory(@RequestBody LoginHistoryRequest loginHistoryRequest){
        return ApiResponse.<LoginHistoryResponse>builder()
                .EC(0)
                .EM("Tạo lịch sử đăng nhập thành công")
                .result(loginHistoryService.createLoginHistory(loginHistoryRequest))
                .build();
    }

    @PatchMapping("/update/{loginHistoryId}")
    ApiResponse<LoginHistoryResponse> updateLoginHistory(@PathVariable String loginHistoryId, @RequestBody LoginActivityRequest loginActivityRequest){

        return ApiResponse.<LoginHistoryResponse>builder()
                .EC(0)
                .EM("Cập nhật hoạt động thành công")
                .result(loginHistoryService.updateLoginHistory(loginHistoryId, loginActivityRequest))
                .build();
    }

    @GetMapping("/get-all")
    ApiResponse<List<LoginHistoryResponse>> getAllLoginHistory(){
        return ApiResponse.<List<LoginHistoryResponse>>builder()
                .EC(0)
                .EM("Lấy hoạt động thành công")
                .result(loginHistoryService.getAllLoginHistory())
                .build();
    }

    @GetMapping("/get-detail/{loginHistoryId}")
    ApiResponse<LoginHistoryResponse> getDetailLoginHistory(@PathVariable String loginHistoryId){
        return ApiResponse.<LoginHistoryResponse>builder()
                .EC(0)
                .EM("Lấy chi tiết hoạt động thành công")
                .result(loginHistoryService.getDetailLoginHistory(loginHistoryId))
                .build();
    }
}
