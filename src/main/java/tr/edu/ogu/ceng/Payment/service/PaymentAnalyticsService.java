package tr.edu.ogu.ceng.Payment.service;

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

    public void deleteById(Long id) {
        paymentAnalyticsRepository.deleteById(id);
    }
}
