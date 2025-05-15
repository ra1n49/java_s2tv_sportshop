package com.s2tv.sportshop.model;

import com.s2tv.sportshop.enums.NotifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    private String id;
    private String userId;
    private String orderId;
    private String productId;
    private String discountId;
    NotifyType notifyType;
    private String notifyDescription;
    private boolean isRead;
    private String redirectUrl;
}
