package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentAnalyticsDTO;
import tr.edu.ogu.ceng.payment.service.PaymentAnalyticsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-analytics")
public class PaymentAnalyticsController {

    private final PaymentAnalyticsService paymentAnalyticsService;

    @GetMapping
    public List<PaymentAnalyticsDTO> getAllPaymentAnalytics() {
        return paymentAnalyticsService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentAnalyticsDTO> getPaymentAnalytics(@PathVariable Long id) {
        return paymentAnalyticsService.findById(id);
    }

    @PostMapping
    public PaymentAnalyticsDTO createPaymentAnalytics(@RequestBody PaymentAnalyticsDTO paymentAnalyticsDTO) {
        return paymentAnalyticsService.save(paymentAnalyticsDTO);
    }

    @PutMapping("/{id}")
    public PaymentAnalyticsDTO updatePaymentAnalytics(@PathVariable Long id, @RequestBody PaymentAnalyticsDTO paymentAnalyticsDTO) {
        paymentAnalyticsDTO.setAnalyticsId(id);  // ID'yi ayarla
        return paymentAnalyticsService.save(paymentAnalyticsDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeletePaymentAnalytics(@PathVariable Long id) {
        paymentAnalyticsService.softDelete(id, "system"); // "system" yerine kullanıcı bilgisi eklenebilir
    }
}
