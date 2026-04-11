package kizuna.audit.controller;

import kizuna.audit.domain.Audit;
import kizuna.audit.repository.AuditRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {
    private AuditRepository auditRepository;

    public AuditController(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @GetMapping
    public ResponseEntity<List<Audit>> getAll() {
        return ResponseEntity.ok(auditRepository.findAll());
    }
    @GetMapping("/entity/{entity}")
    public ResponseEntity<Audit> findByEntity(@PathVariable String entity) {
        return ResponseEntity.ok(auditRepository.findById(entity).orElse(null));
    }

    @GetMapping("/entity-id/{id}")
    public ResponseEntity<Audit> findByEntityId(@PathVariable String id) {
        return ResponseEntity.ok(auditRepository.findByEntityId(id));
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<Audit> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(auditRepository.findByUsername(username).orElse(null));
    }

}
