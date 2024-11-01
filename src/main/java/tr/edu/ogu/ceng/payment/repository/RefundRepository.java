package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.entity.Refund;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
}
