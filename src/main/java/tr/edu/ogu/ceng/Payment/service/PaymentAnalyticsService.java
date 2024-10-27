package tr.edu.ogu.ceng.Payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.PaymentAnalytics;
import tr.edu.ogu.ceng.Payment.repository.PaymentAnalyticsRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentAnalyticsService {

    private final PaymentAnalyticsRepository paymentAnalyticsRepository;

    public List<PaymentAnalytics> findAll() {
        return paymentAnalyticsRepository.findAll();
    }

    public Optional<PaymentAnalytics> findById(Long id) {
        return paymentAnalyticsRepository.findById(id);
    }

    public PaymentAnalytics save(PaymentAnalytics paymentAnalytics) {
        return paymentAnalyticsRepository.save(paymentAnalytics);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentAnalytics> paymentAnalyticsOptional = paymentAnalyticsRepository.findById(id);
        if (paymentAnalyticsOptional.isPresent()) {
            PaymentAnalytics paymentAnalytics = paymentAnalyticsOptional.get();
            paymentAnalytics.setDeletedAt(java.time.LocalDateTime.now());
            paymentAnalytics.setDeletedBy(deletedBy);
            paymentAnalyticsRepository.save(paymentAnalytics);
        }
    }
}
