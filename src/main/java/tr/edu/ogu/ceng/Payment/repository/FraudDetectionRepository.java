package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.Payment.model.FraudDetection;

@Repository
public interface FraudDetectionRepository extends JpaRepository<FraudDetection, Long> {
}