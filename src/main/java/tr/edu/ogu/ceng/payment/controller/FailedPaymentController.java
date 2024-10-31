    package tr.edu.ogu.ceng.payment.controller;

    import lombok.RequiredArgsConstructor;
    import org.springframework.web.bind.annotation.*;
    import tr.edu.ogu.ceng.payment.model.FailedPayment;
    import tr.edu.ogu.ceng.payment.service.FailedPaymentService;

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

        // Soft delete işlemi için güncellenmiş endpoint
        @DeleteMapping("/{id}")
        public void softDeleteFailedPayment(@PathVariable Long id) {
            failedPaymentService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
        }
    }
