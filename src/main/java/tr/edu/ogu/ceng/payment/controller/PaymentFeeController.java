package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.model.PaymentFee;
import tr.edu.ogu.ceng.payment.service.PaymentFeeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-fee")
public class PaymentFeeController {

    private final PaymentFeeService paymentFeeService;

    @GetMapping
    public List<PaymentFee> getAllPaymentFees() {
        return paymentFeeService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentFee> getPaymentFee(@PathVariable Long id) {
        return paymentFeeService.findById(id);
    }

    @PostMapping
    public PaymentFee createPaymentFee(@RequestBody PaymentFee paymentFee) {
        return paymentFeeService.save(paymentFee);
    }

    @PutMapping("/{id}")
    public PaymentFee updatePaymentFee(@PathVariable Long id, @RequestBody PaymentFee paymentFee) {
        paymentFee.setFeeId(id);  // ID'yi set et
        return paymentFeeService.save(paymentFee);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeletePaymentFee(@PathVariable Long id) {
        paymentFeeService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
