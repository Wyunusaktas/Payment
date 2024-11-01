package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.entity.ThirdPartyPayment;

@Repository
public interface ThirdPartyPaymentRepository extends JpaRepository<ThirdPartyPayment, Long> {
}
