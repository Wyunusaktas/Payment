package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.PaymentFee;

public interface PaymentFeeRepository extends JpaRepository<PaymentFee, Long> {
}
