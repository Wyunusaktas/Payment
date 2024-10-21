package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
