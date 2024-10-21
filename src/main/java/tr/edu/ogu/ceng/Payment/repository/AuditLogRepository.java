package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
