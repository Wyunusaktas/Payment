package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.AuditLog;
import tr.edu.ogu.ceng.Payment.service.AuditLogService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public List<AuditLog> getAllAuditLogs() {
        return auditLogService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<AuditLog> getAuditLog(@PathVariable Long id) {
        return auditLogService.findById(id);
    }

    @PostMapping
    public AuditLog createAuditLog(@RequestBody AuditLog auditLog) {
        return auditLogService.save(auditLog);
    }

    @PutMapping("/{id}")
    public AuditLog updateAuditLog(@PathVariable Long id, @RequestBody AuditLog auditLog) {
        auditLog.setLogId(id);  // ID'yi set et
        return auditLogService.save(auditLog);
    }

    @DeleteMapping("/{id}")
    public void deleteAuditLog(@PathVariable Long id) {
        auditLogService.deleteById(id);
    }
}
