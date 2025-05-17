package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.NotifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "Notification")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String orderId;
    private String productId;
    private String discountId;
    NotifyType notifyType;
    private String notifyTitle;
    private String notifyDescription;
    private boolean isRead;
    private String redirectUrl;
    private String imageUrl;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date updatedAt;
}
