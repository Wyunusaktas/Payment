package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.PaymentMethod;
import tr.edu.ogu.ceng.Payment.service.PaymentMethodService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-method")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentMethod> getPaymentMethod(@PathVariable Long id) {
        return paymentMethodService.findById(id);
    }

    @PostMapping
    public PaymentMethod createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        return paymentMethodService.save(paymentMethod);
    }

    @PutMapping("/{id}")
    public PaymentMethod updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethod paymentMethod) {
        paymentMethod.setMethodId(id);  // ID'yi set et
        return paymentMethodService.save(paymentMethod);
    }

    @DeleteMapping("/{id}")
    public void deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deleteById(id);
    }
}
