package com.s2tv.sportshop.dto.response;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
    private String id;
    private String productId;
    private String color;
    private String variant;
    private String orderId;
    private String userId;
    private String content;
    private Integer rating;
    private String repliedByAdmin;
    private FeedbackMediaResponse feedbackMedia;
    private Date createdAt;
}
