package kizuna_notification_service.repository;

import kizuna_notification_service.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdAndIsReadFalseOrderByTimestampDesc(String userId);
}
