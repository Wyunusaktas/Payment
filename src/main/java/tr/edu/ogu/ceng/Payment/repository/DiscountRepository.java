package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
