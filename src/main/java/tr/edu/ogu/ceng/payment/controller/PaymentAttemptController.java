package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentAttemptDTO;
import tr.edu.ogu.ceng.payment.service.PaymentAttemptService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-attempt")
public class PaymentAttemptController {

    private final PaymentAttemptService paymentAttemptService;

    @GetMapping
    public List<PaymentAttemptDTO> getAllPaymentAttempts() {
        return paymentAttemptService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentAttemptDTO> getPaymentAttempt(@PathVariable Long id) {
        return paymentAttemptService.findById(id);
    }

    @PostMapping
    public PaymentAttemptDTO createPaymentAttempt(@RequestBody PaymentAttemptDTO paymentAttemptDTO) {
        return paymentAttemptService.save(paymentAttemptDTO);
    }

    @PutMapping("/{id}")
    public PaymentAttemptDTO updatePaymentAttempt(@PathVariable Long id, @RequestBody PaymentAttemptDTO paymentAttemptDTO) {
        paymentAttemptDTO.setAttemptId(id);  // ID'yi ayarla
        return paymentAttemptService.save(paymentAttemptDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeletePaymentAttempt(@PathVariable Long id) {
        paymentAttemptService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
