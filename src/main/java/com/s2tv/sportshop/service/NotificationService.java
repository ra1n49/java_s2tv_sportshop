package com.s2tv.sportshop.service;


import com.s2tv.sportshop.dto.request.NotificationRequest;
import com.s2tv.sportshop.dto.response.NotificationResponse;
import com.s2tv.sportshop.exception.AppException;
import com.s2tv.sportshop.mapper.NotificationMapper;
import com.s2tv.sportshop.model.Notification;
import com.s2tv.sportshop.repository.NotificationRepository;
import com.s2tv.sportshop.repository.OrderRepository;
import com.s2tv.sportshop.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.s2tv.sportshop.exception.ErrorCode.NOTIFICATION_NOTFOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    public NotificationResponse createNotification(String userId, NotificationRequest notificationRequest) {
        Notification notification = Notification.builder()
                .orderId(notificationRequest.getOrderId())
                .discountId(notificationRequest.getDiscountId())
                .productId(notificationRequest.getProductId())
                .notifyType(notificationRequest.getNotifyType())
                .userId(userId)
                .notifyTitle(notificationRequest.getNotifyTitle())
                .notifyDescription(notificationRequest.getNotifyDescription())
                .imageUrl(notificationRequest.getImageUrl())
                .read(false)
                .build();
        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toNotificationResponse(savedNotification);
    }

    public List<NotificationResponse> getNotificationsByUser(String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(NOTIFICATION_NOTFOUND));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(String notificationId, String currentUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(NOTIFICATION_NOTFOUND));

        if ((notification.getUserId() == null || !notification.getUserId().equals(currentUserId))) {
            throw new SecurityException("Bạn không có quyền xóa thông báo này");
        }

        notificationRepository.deleteById(notificationId);
    }
}
