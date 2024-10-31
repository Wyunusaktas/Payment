package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
