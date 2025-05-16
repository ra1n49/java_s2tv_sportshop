package com.s2tv.sportshop.dto.response;

import com.s2tv.sportshop.enums.NotifyType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    String userId;
    String orderId;
    String productId;
    String discountId;
    NotifyType notifyType;
    String notifyDescription;
    boolean isRead;
    String redirectUrl;
}
