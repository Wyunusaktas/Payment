package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentDTO;
import tr.edu.ogu.ceng.payment.service.PaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentDTO> getPayment(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public PaymentDTO createPayment(@RequestBody PaymentDTO paymentDTO) {
        return paymentService.save(paymentDTO);
    }

    @PutMapping("/{id}")
    public PaymentDTO updatePayment(@PathVariable Long id, @RequestBody PaymentDTO paymentDTO) {
        paymentDTO.setPaymentId(id);  // ID'yi ayarla
        return paymentService.save(paymentDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeletePayment(@PathVariable Long id) {
        paymentService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
