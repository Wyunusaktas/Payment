package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.PaymentAttempt;
import tr.edu.ogu.ceng.Payment.service.PaymentAttemptService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-attempt")
public class PaymentAttemptController {

    private final PaymentAttemptService paymentAttemptService;

    @GetMapping
    public List<PaymentAttempt> getAllPaymentAttempts() {
        return paymentAttemptService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentAttempt> getPaymentAttempt(@PathVariable Long id) {
        return paymentAttemptService.findById(id);
    }

    @PostMapping
    public PaymentAttempt createPaymentAttempt(@RequestBody PaymentAttempt paymentAttempt) {
        return paymentAttemptService.save(paymentAttempt);
    }

    @PutMapping("/{id}")
    public PaymentAttempt updatePaymentAttempt(@PathVariable Long id, @RequestBody PaymentAttempt paymentAttempt) {
        paymentAttempt.setAttemptId(id);  // ID'yi set et
        return paymentAttemptService.save(paymentAttempt);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeletePaymentAttempt(@PathVariable Long id) {
        paymentAttemptService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
