package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.model.AuditLog;
import tr.edu.ogu.ceng.payment.repository.AuditLogRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    public Optional<AuditLog> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<AuditLog> auditLogOptional = auditLogRepository.findById(id);
        if (auditLogOptional.isPresent()) {
            AuditLog auditLog = auditLogOptional.get();
            auditLog.setDeletedAt(java.time.LocalDateTime.now());
            auditLog.setDeletedBy(deletedBy);
            auditLogRepository.save(auditLog);
        }
    }
}
