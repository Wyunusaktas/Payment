package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.ThirdPartyPaymentDTO;
import tr.edu.ogu.ceng.payment.service.ThirdPartyPaymentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/third-party-payment")
public class ThirdPartyPaymentController {

    private final ThirdPartyPaymentService thirdPartyPaymentService;

    @GetMapping
    public List<ThirdPartyPaymentDTO> getAllThirdPartyPayments() {
        return thirdPartyPaymentService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ThirdPartyPaymentDTO> getThirdPartyPayment(@PathVariable Long id) {
        return thirdPartyPaymentService.findById(id);
    }

    @PostMapping
    public ThirdPartyPaymentDTO createThirdPartyPayment(@RequestBody ThirdPartyPaymentDTO thirdPartyPaymentDTO) {
        return thirdPartyPaymentService.save(thirdPartyPaymentDTO);
    }

    @PutMapping("/{id}")
    public ThirdPartyPaymentDTO updateThirdPartyPayment(@PathVariable Long id, @RequestBody ThirdPartyPaymentDTO thirdPartyPaymentDTO) {
        thirdPartyPaymentDTO.setThirdPartyPaymentId(id);  // ID'yi ayarla
        return thirdPartyPaymentService.save(thirdPartyPaymentDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteThirdPartyPayment(@PathVariable Long id) {
        thirdPartyPaymentService.softDelete(id, "system");
    }
}
