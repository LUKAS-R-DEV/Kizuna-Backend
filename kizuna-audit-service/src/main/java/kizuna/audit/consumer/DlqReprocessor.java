package kizuna.audit.consumer;

import kizuna.audit.domain.Audit;
import kizuna.audit.repository.AuditRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class DlqReprocessor {

    private final AuditRepository auditRepository;

    public DlqReprocessor(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @RabbitListener(queues = "audit.dlq")
    public void reprocess(Map<String, Object> event) {

        try {
            System.out.println("🔁 Tentando reprocessar evento DLQ...");

            Audit audit = Audit.builder()
                    .version((String) event.get("version"))
                    .action((String) event.get("action"))
                    .entity((String) event.get("entity"))
                    .entityId((String) event.get("entityId"))
                    .username((String) event.get("username"))
                    .timestamp(LocalDateTime.parse((String) event.get("timestamp")))
                    .details((Map<String, Object>) event.get("data"))
                    .build();

            auditRepository.save(audit);

            System.out.println("✅ Evento reprocessado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Falha ao reprocessar DLQ. Evento descartado: " + event);
        }
    }
}
