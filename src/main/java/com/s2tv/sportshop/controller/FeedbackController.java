package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.FeedbackCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.model.Feedback;
import com.s2tv.sportshop.service.FeedbackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Feedback> createFeedback(@ModelAttribute FeedbackCreateRequest request) {
        Feedback feedback = feedbackService.createFeedback(request);
        return ApiResponse.<Feedback>builder()
                .EC(0)
                .EM("Tạo feedback thành công")
                .result(feedback)
                .build();
    }

    @GetMapping("/get-all/{productId}")
    public ApiResponse<List<Feedback>> getAllFeedbacks(@PathVariable String productId) {
        List<Feedback> feedback = feedbackService.getFeedbacks(productId);
        return ApiResponse.<List<Feedback>>builder()
                .EC(0)
                .EM("Lấy danh sách feedback cho sản phẩm thành công")
                .result(feedback)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{feedbackId}")
    public ApiResponse<String> deleteFeedback(@PathVariable String feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ApiResponse.<String>builder()
                .EC(0)
                .EM("Xóa feedback thành công")
                .build();
    }
}
