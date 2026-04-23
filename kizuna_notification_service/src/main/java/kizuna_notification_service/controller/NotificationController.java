package kizuna_notification_service.controller;

import kizuna_notification_service.domain.Notification;
import kizuna_notification_service.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationRepository notificationRepository;



    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        return ResponseEntity.ok(notifications);
    }

}
