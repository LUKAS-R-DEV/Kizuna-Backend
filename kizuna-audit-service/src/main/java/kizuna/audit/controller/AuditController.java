package kizuna.audit.controller;

import kizuna.audit.domain.Audit;
import kizuna.audit.repository.AuditRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditRepository auditRepository;

    public AuditController(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @GetMapping
    public ResponseEntity<List<Audit>> getAll() {
        return ResponseEntity.ok(auditRepository.findAll());
    }

    @GetMapping("/entity/{entity}")
    public ResponseEntity<List<Audit>> findByEntity(@PathVariable String entity) {
        return ResponseEntity.ok(auditRepository.findByEntity(entity));
    }

    @GetMapping("/entity-id/{id}")
    public ResponseEntity<List<Audit>> findByEntityId(@PathVariable String id) {
        return ResponseEntity.ok(auditRepository.findByEntityId(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Audit>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(auditRepository.findByUsername(username));
    }
}