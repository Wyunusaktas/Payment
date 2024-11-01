package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentMethodDTO;
import tr.edu.ogu.ceng.payment.service.PaymentMethodService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-method")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return paymentMethodService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentMethodDTO> getPaymentMethod(@PathVariable Long id) {
        return paymentMethodService.findById(id);
    }

    @PostMapping
    public PaymentMethodDTO createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.save(paymentMethodDTO);
    }

    @PutMapping("/{id}")
    public PaymentMethodDTO updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethodDTO paymentMethodDTO) {
        paymentMethodDTO.setMethodId(id);  // ID'yi ayarla
        return paymentMethodService.save(paymentMethodDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
