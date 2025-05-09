package com.s2tv.sportshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackCreateRequest {
    private String productId;
    private String color;
    private String variant;
    private String orderId;
    private String userId;
    private String content;
    private Integer rating;

    private MultipartFile[] images;
    private MultipartFile[] videos;
}
