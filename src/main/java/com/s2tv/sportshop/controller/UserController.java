package com.s2tv.sportshop.controller;


import com.cloudinary.Api;
import com.s2tv.sportshop.dto.request.ChangePasswordRequest;
import com.s2tv.sportshop.dto.request.UserUpdateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.UserResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.model.Address;
import com.s2tv.sportshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
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
    @PutMapping("/")
    public ApiResponse<UserResponse> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody UserUpdateRequest userUpdateData) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<UserResponse>builder()
                .EC(0)
                .EM("Cập nhật thông tin thành công")
                .result(userService.updateUser(userId, userUpdateData))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update-avatar")
    public ApiResponse<String> updateAvatar(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestPart("file") MultipartFile file){
        String userId = userPrincipal.getUser().getId();
        String imageUrl = userService.updateAvatar(userId, file);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Cập nhật ảnh đại diện thành công")
                .result(imageUrl)
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
}
