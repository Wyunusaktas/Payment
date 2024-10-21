package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.Payment.model.PaymentFee;

@Repository
public interface PaymentFeeRepository extends JpaRepository<PaymentFee, Long> {
}
