package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.PaymentAnalytics;
import tr.edu.ogu.ceng.Payment.service.PaymentAnalyticsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-analytics")
public class PaymentAnalyticsController {

    private final PaymentAnalyticsService paymentAnalyticsService;

    @GetMapping
    public List<PaymentAnalytics> getAllPaymentAnalytics() {
        return paymentAnalyticsService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentAnalytics> getPaymentAnalytics(@PathVariable Long id) {
        return paymentAnalyticsService.findById(id);
    }

    @PostMapping
    public PaymentAnalytics createPaymentAnalytics(@RequestBody PaymentAnalytics paymentAnalytics) {
        return paymentAnalyticsService.save(paymentAnalytics);
    }

    @PutMapping("/{id}")
    public PaymentAnalytics updatePaymentAnalytics(@PathVariable Long id, @RequestBody PaymentAnalytics paymentAnalytics) {
        paymentAnalytics.setAnalyticsId(id);  // ID'yi set et
        return paymentAnalyticsService.save(paymentAnalytics);
    }

    @DeleteMapping("/{id}")
    public void deletePaymentAnalytics(@PathVariable Long id) {
        paymentAnalyticsService.deleteById(id);
    }
}
