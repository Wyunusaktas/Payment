package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.ThirdPartyPayment;
import tr.edu.ogu.ceng.Payment.service.ThirdPartyPaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/third-party-payment")
public class ThirdPartyPaymentController {

    private final ThirdPartyPaymentService thirdPartyPaymentService;

    @GetMapping
    public List<ThirdPartyPayment> getAllThirdPartyPayments() {
        return thirdPartyPaymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ThirdPartyPayment> getThirdPartyPayment(@PathVariable Long id) {
        return thirdPartyPaymentService.findById(id);
    }

    @PostMapping
    public ThirdPartyPayment createThirdPartyPayment(@RequestBody ThirdPartyPayment thirdPartyPayment) {
        return thirdPartyPaymentService.save(thirdPartyPayment);
    }

    @PutMapping("/{id}")
    public ThirdPartyPayment updateThirdPartyPayment(@PathVariable Long id, @RequestBody ThirdPartyPayment thirdPartyPayment) {
        thirdPartyPayment.setThirdPartyPaymentId(id);  // ID'yi set et
        return thirdPartyPaymentService.save(thirdPartyPayment);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeleteThirdPartyPayment(@PathVariable Long id) {
        thirdPartyPaymentService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
