package kizuna.audit.repository;

import kizuna.audit.domain.Audit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditRepository  extends MongoRepository<Audit, String> {
    List<Audit> findByEntity(String entity);
    List<Audit> findByEntityId(String entityId);
    List<Audit> findByUsername(String username);
}
