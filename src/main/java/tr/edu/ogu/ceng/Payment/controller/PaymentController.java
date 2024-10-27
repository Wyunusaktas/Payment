package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Payment;
import tr.edu.ogu.ceng.Payment.service.PaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Payment> getPayment(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.save(payment);
    }

    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        payment.setPaymentId(id);  // ID'yi ayarla
        return paymentService.save(payment);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeletePayment(@PathVariable Long id) {
        paymentService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
