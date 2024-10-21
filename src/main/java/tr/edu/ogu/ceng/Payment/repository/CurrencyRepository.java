package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
