package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.model.PaymentFee;

@Repository
public interface PaymentFeeRepository extends JpaRepository<PaymentFee, Long> {
}
