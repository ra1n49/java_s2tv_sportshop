package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.NotificationRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.NotificationResponse;
import com.s2tv.sportshop.filter.UserPrincipal;
import com.s2tv.sportshop.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<NotificationResponse> createNotification(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @RequestBody NotificationRequest notificationRequest) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<NotificationResponse>builder()
                .EC(0)
                .EM("Tạo thông báo cho tất cả người dùng thành công")
                .result(notificationService.createNotification(userId, notificationRequest))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-user-notifications")
    public ApiResponse<List<NotificationResponse>> getNotificationByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        return ApiResponse.<List<NotificationResponse>>builder()
                .EC(0)
                .EM("Lấy thông báo của người dùng thành công")
                .result(notificationService.getNotificationsByUser(userId))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/read/{notificationId}")
    public ApiResponse<Void> readNotificationById(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Đọc thông báo thành công")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotificationById(@PathVariable String notificationId,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = userPrincipal.getUser().getId();
        notificationService.deleteNotification(notificationId, userId);
        return ApiResponse.<Void>builder()
                .EC(0)
                .EM("Xóa thông báo thành công")
                .build();
    }
}
