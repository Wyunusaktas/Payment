package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.ChargebackDTO;
import tr.edu.ogu.ceng.payment.service.ChargebackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chargeback")
public class ChargebackController {

    private final ChargebackService chargebackService;

    @GetMapping
    public List<ChargebackDTO> getAllChargebacks() {
        return chargebackService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ChargebackDTO> getChargeback(@PathVariable Long id) {
        return chargebackService.findById(id);
    }

    @PostMapping
    public ChargebackDTO createChargeback(@RequestBody ChargebackDTO chargebackDTO) {
        return chargebackService.save(chargebackDTO);
    }

    @PutMapping("/{id}")
    public ChargebackDTO updateChargeback(@PathVariable Long id, @RequestBody ChargebackDTO chargebackDTO) {
        chargebackDTO.setChargebackId(id);  // ID'yi ayarla
        return chargebackService.save(chargebackDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteChargeback(@PathVariable Long id) {
        chargebackService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
