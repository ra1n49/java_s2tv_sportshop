package com.s2tv.sportshop.mapper;

import com.s2tv.sportshop.dto.request.NotificationRequest;
import com.s2tv.sportshop.dto.response.NotificationResponse;
import com.s2tv.sportshop.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toNotification(NotificationRequest request);

    @Mapping(source = "read", target = "read")
    NotificationResponse toNotificationResponse(Notification notification);
    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}