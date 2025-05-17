package com.s2tv.sportshop.dto.request;

import com.s2tv.sportshop.enums.NotifyType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    String orderId;
    String productId;
    String discountId;
    NotifyType notifyType;
    String notifyTitle;
    String notifyDescription;
    String redirectUrl;
    String imageUrl;
}
