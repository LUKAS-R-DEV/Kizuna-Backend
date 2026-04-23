package kizuna_notification_service.listener;

import kizuna_notification_service.domain.Notification;
import kizuna_notification_service.dto.GenericEventDto;
import kizuna_notification_service.processor.NotificationProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationProcessor notificationProcessor;

    @RabbitListener(queues = "notification.queue")
    public void consume(GenericEventDto event) {
        notificationProcessor.process(event);
        System.out.println("[NOTIFICATION] Evento processado: " + event);
    }
}
