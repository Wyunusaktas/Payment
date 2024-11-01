package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.AuditLogDTO;
import tr.edu.ogu.ceng.payment.service.AuditLogService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAllAuditLogs() {
        List<AuditLogDTO> auditLogs = auditLogService.findAll();
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDTO> getAuditLog(@PathVariable Long id) {
        Optional<AuditLogDTO> auditLogDTO = auditLogService.findById(id);
        return auditLogDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuditLogDTO> createAuditLog(@RequestBody AuditLogDTO auditLogDTO) {
        AuditLogDTO createdAuditLog = auditLogService.save(auditLogDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuditLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditLogDTO> updateAuditLog(@PathVariable Long id, @RequestBody AuditLogDTO auditLogDTO) {
        auditLogDTO.setLogId(id);  // ID'yi ayarla
        AuditLogDTO updatedAuditLog = auditLogService.save(auditLogDTO);
        return ResponseEntity.ok(updatedAuditLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteAuditLog(@PathVariable Long id) {
        auditLogService.softDelete(id, "system");
        return ResponseEntity.noContent().build();
    }
}
