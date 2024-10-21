package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Chargeback;

public interface ChargebackRepository extends JpaRepository<Chargeback, Long> {
}
