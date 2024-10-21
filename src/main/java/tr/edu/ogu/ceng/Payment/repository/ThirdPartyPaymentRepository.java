package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.ThirdPartyPayment;

public interface ThirdPartyPaymentRepository extends JpaRepository<ThirdPartyPayment, Long> {
}
