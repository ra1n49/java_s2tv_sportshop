package com.s2tv.sportshop.controller;

import com.s2tv.sportshop.dto.request.FeedbackCreateRequest;
import com.s2tv.sportshop.dto.response.ApiResponse;
import com.s2tv.sportshop.dto.response.FeedbackResponse;
import com.s2tv.sportshop.service.FeedbackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {

    FeedbackService feedbackService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FeedbackResponse> createFeedback( @ModelAttribute FeedbackCreateRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .result(feedbackService.createFeedback(request))
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<FeedbackResponse>> getAllFeedbacks(@PathVariable String productId) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getFeedbacks(productId))
                .build();
    }

    @DeleteMapping("/{feedbackId}")
    public ApiResponse<Void> deleteFeedback(@PathVariable String feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ApiResponse.<Void>builder().result(null).build();
    }
}
