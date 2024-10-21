package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.FraudDetection;

public interface FraudDetectionRepository extends JpaRepository<FraudDetection, Long> {
}
