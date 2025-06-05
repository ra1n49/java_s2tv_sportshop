package com.s2tv.sportshop.controller;


import com.cloudinary.Api;
import com.s2tv.sportshop.dto.request.ChangePasswordRequest;
import com.s2tv.sportshop.dto.request.UserUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.DiscountResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.model.Address;
import com.s2tv.sportshop.model.ChatHistory;
import com.s2tv.sportshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Lấy thông tin người dùng thành công!")
                .result(userService.getUserById(userId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-user")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .EC(0)
                .EM("Lấy danh sách người dùng thành công!")
                .result(userService.getAllUsers())
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/change-password")
    public ApiResponse<String> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ChangePasswordRequest request) {
        String email = userPrincipal.getUser().getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        userService.changePassword(email, oldPassword, newPassword);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Đổi mật khẩu thành công")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ApiResponse<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserUpdateRequest userUpdateData) throws ParseException {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Cập nhật thông tin thành công")
                .result(userService.updateUser(userId, userUpdateData))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update/profile")
    public ApiResponse<UserResponse> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ModelAttribute UserUpdateRequest userUpdateData) throws ParseException {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Cập nhật thông tin thành công")
                .result(userService.updateUser(userId, userUpdateData))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/address")
    public ApiResponse<UserResponse> addAddress(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody Address address) {
        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Thêm địa chỉ thành công")
                .result(userService.addAddress(userId, address))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/address/{index}")
    public ApiResponse<Address> updateAddress(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable int index, @RequestBody Address addressUpdateData){
        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<Address>builder()
                .EC(0)
                .EM("Cập nhật địa chỉ thành công")
                .result(userService.updateAddress(userId, index, addressUpdateData))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/address/{index}")
    public ApiResponse<String> deleteAddress(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable int index) {
        String userId = userPrincipal.getUser().getId();
        userService.deleteAddress(userId, index);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Xóa địa chỉ thành công")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-discount")
    public ApiResponse<List<DiscountResponse>> getDiscountUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        String userId = userPrincipal.getUser().getId();

        return ApiResponse.<List<DiscountResponse>>builder()
                .EC(0)
                .EM("Lấy mã giảm giá thành công")
                .result(userService.getDiscountUser(userId))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-chat-history")
    public ApiResponse<List<ChatHistory.Message>> getChatHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();

        List<ChatHistory.Message> messages = userService.getChatHistoryByUserId(userId);

        return ApiResponse.<List<ChatHistory.Message>>builder()
                .EC(0)
                .EM("Lấy lịch sử chat thành công")
                .result(messages)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete-search-history/{index}")
    public ApiResponse<Void> deleteSearchHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int index
    ) {
        String userId = userPrincipal.getUser().getId();
        userService.deleteSearchHistory(userId, index);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa lịch sử tìm kiếm thành công")
                .build();
    }
}
