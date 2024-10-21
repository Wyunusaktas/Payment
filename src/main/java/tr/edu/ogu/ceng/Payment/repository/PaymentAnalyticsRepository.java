package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.PaymentAnalytics;

public interface PaymentAnalyticsRepository extends JpaRepository<PaymentAnalytics, Long> {
}
