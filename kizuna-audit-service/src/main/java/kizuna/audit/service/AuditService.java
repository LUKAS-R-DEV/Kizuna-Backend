package kizuna.audit.service;

import kizuna.audit.domain.Audit;
import kizuna.audit.dto.AuditEventDto;
import kizuna.audit.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditRepository auditRepository;


    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void saveAudit(AuditEventDto auditEventDto) {
        Audit log= Audit.builder()
                .action(auditEventDto.action())
                .details(auditEventDto.details())
                .username(auditEventDto.username())
                .entity(auditEventDto.entity())
                .entityId(auditEventDto.entityId())
                .timestamp(auditEventDto.timestamp())
                .userId(auditEventDto.userId())
                .build();

    }




}
