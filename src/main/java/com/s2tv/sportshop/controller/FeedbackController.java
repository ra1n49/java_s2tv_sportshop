package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.FeedbackCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.model.Feedback;
import com.s2tv.sportshop.service.FeedbackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Feedback> createFeedback(@ModelAttribute FeedbackCreateRequest request) {
        Feedback feedback = feedbackService.createFeedback(request);
        return ApiResponse.<Feedback>builder()
                .result(feedback)
                .build();
    }

    @GetMapping("/get/{productId}")
    public ApiResponse<List<Feedback>> getAllFeedbacks(@PathVariable String productId) {
        List<Feedback> feedback = feedbackService.getFeedbacks(productId);
        return ApiResponse.<List<Feedback>>builder()
                .result(feedback)
                .build();
    }

    @DeleteMapping("/delete/{feedbackId}")
    public ApiResponse<Void> deleteFeedback(@PathVariable String feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }
}
