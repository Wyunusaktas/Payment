package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.PaymentAttempt;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {
}
