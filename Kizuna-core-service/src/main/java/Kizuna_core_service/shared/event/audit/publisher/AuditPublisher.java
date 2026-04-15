package Kizuna_core_service.shared.event.audit.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuditPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AuditPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String action, String entity, String entityId, String username, Object data) {

        Map<String, Object> event = new HashMap<>();

        event.put("version", "v1");
        event.put("action", action);
        event.put("entity", entity);
        event.put("entityId", entityId);
        event.put("username", username);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("data", data);

        rabbitTemplate.convertAndSend("audit.exchange", "audit.key", event);
    }
}
