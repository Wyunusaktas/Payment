package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
