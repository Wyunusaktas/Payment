package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentFeeDTO;
import tr.edu.ogu.ceng.payment.service.PaymentFeeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-fee")
public class PaymentFeeController {

    private final PaymentFeeService paymentFeeService;

    @GetMapping
    public List<PaymentFeeDTO> getAllPaymentFees() {
        return paymentFeeService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<PaymentFeeDTO> getPaymentFee(@PathVariable Long id) {
        return paymentFeeService.findById(id);
    }

    @PostMapping
    public PaymentFeeDTO createPaymentFee(@RequestBody PaymentFeeDTO paymentFeeDTO) {
        return paymentFeeService.save(paymentFeeDTO);
    }

    @PutMapping("/{id}")
    public PaymentFeeDTO updatePaymentFee(@PathVariable Long id, @RequestBody PaymentFeeDTO paymentFeeDTO) {
        paymentFeeDTO.setFeeId(id);  // ID'yi set et
        return paymentFeeService.save(paymentFeeDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeletePaymentFee(@PathVariable Long id) {
        paymentFeeService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
