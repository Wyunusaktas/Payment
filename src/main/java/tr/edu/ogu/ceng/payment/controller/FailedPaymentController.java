package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.FailedPaymentDTO;
import tr.edu.ogu.ceng.payment.service.FailedPaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/failed-payment")
public class FailedPaymentController {

    private final FailedPaymentService failedPaymentService;

    @GetMapping
    public List<FailedPaymentDTO> getAllFailedPayments() {
        return failedPaymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<FailedPaymentDTO> getFailedPayment(@PathVariable Long id) {
        return failedPaymentService.findById(id);
    }

    @PostMapping
    public FailedPaymentDTO createFailedPayment(@RequestBody FailedPaymentDTO failedPaymentDTO) {
        return failedPaymentService.save(failedPaymentDTO);
    }

    @PutMapping("/{id}")
    public FailedPaymentDTO updateFailedPayment(@PathVariable Long id, @RequestBody FailedPaymentDTO failedPaymentDTO) {
        failedPaymentDTO.setFailedPaymentId(id);  // ID'yi ayarla
        return failedPaymentService.save(failedPaymentDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteFailedPayment(@PathVariable Long id) {
        failedPaymentService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
