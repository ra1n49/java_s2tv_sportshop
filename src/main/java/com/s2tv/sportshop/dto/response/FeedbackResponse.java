package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.s2tv.sportshop.model.User;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackResponse {
    private String id;
    private String productId;
    private String color;
    private String variant;
    private String orderId;
    private String userId;
    private User user;
    private String content;
    private Integer rating;
    private String repliedByAdmin;
    private FeedbackMediaResponse feedbackMedia;
    private Date createdAt;
}
