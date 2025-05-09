package com.s2tv.sportshop.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;

@Document(collection = "Feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private String id;

    private String productId;

    private String color;

    private String variant;

    private String orderId;

    private String userId;

    private String content;

    private FeedbackMedia feedbackMedia;

    private Integer rating;

    private String repliedByAdmin;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Date updatedAt;

    @Field("deleted")
    private boolean deleted = false;

    @Field("deleted_at")
    private Date deletedAt;
}
