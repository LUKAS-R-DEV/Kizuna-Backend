package kizuna.audit.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@RequiredArgsConstructor
@Builder
@Document(collection = "audit_logs")

public class Audit {
    @Id
    private String id;
    private String version;
    private String action;
    private String entity;
    private String entityId;

    private String username;
    private String userId;

    private LocalDateTime timestamp;

    private Map<String, Object> details;
}
