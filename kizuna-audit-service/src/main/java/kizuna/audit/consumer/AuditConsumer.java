package kizuna.audit.consumer;

import kizuna.audit.config.RabbitConfig;
import kizuna.audit.dto.AuditEventDto;
import kizuna.audit.service.AuditService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditConsumer {

    private final AuditService auditService;

    public AuditConsumer(AuditService auditService) {
        this.auditService = auditService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consumeAuditEvent(AuditEventDto auditEventDto) {
        auditService.saveAudit(auditEventDto);
    }
}
