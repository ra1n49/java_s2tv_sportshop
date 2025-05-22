package com.s2tv.sportshop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s2tv.sportshop.enums.NotifyType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

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
    String notifyTitle;
    String notifyDescription;

    @Field("isRead")
    @JsonProperty("isRead")
    boolean read;
    String redirectUrl;
    String imageUrl;
}
