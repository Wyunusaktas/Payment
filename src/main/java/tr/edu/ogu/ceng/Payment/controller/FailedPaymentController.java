package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.FailedPayment;
import tr.edu.ogu.ceng.Payment.service.FailedPaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/failed-payment")
public class FailedPaymentController {

    private final FailedPaymentService failedPaymentService;

    @GetMapping
    public List<FailedPayment> getAllFailedPayments() {
        return failedPaymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<FailedPayment> getFailedPayment(@PathVariable Long id) {
        return failedPaymentService.findById(id);
    }

    @PostMapping
    public FailedPayment createFailedPayment(@RequestBody FailedPayment failedPayment) {
        return failedPaymentService.save(failedPayment);
    }

    @PutMapping("/{id}")
    public FailedPayment updateFailedPayment(@PathVariable Long id, @RequestBody FailedPayment failedPayment) {
        failedPayment.setFailedPaymentId(id);  // ID'yi set et
        return failedPaymentService.save(failedPayment);
    }

    @DeleteMapping("/{id}")
    public void deleteFailedPayment(@PathVariable Long id) {
        failedPaymentService.deleteById(id);
    }
}
