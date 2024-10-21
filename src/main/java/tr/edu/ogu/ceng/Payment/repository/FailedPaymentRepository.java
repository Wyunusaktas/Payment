package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.FailedPayment;

public interface FailedPaymentRepository extends JpaRepository<FailedPayment, Long> {
}
