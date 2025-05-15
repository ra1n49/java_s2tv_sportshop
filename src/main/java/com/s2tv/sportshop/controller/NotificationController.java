package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.NotificationRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.NotificationResponse;
import com.s2tv.sportshop.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PostMapping("/create")
    public ApiResponse<NotificationResponse> createNotification(@RequestParam String userId, @RequestBody NotificationRequest notificationRequest) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.createNotification(userId, notificationRequest))
                .build();
    }

    @GetMapping("/get/{userId}")
    public ApiResponse<List<NotificationResponse>> getNotificationByUser(@PathVariable String userId) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getNotificationsByUser(userId))
                .build();
    }

    @GetMapping("/{notificationId}")
    public ApiResponse<NotificationResponse> getNotificationById(@PathVariable String notificationId) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.getNotificationById(notificationId))
                .build();
    }

    @PatchMapping("/read/{notificationId}")
    public ApiResponse<Void> readNotificationById(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

    @DeleteMapping("/delete/{notificationId}")
    public ApiResponse<Void> deleteNotificationById(@PathVariable String notificationId, @RequestParam String userId) {
        notificationService.deleteNotification(notificationId, userId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }
}
