package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Chargeback;
import tr.edu.ogu.ceng.Payment.service.ChargebackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chargeback")
public class ChargebackController {

    private final ChargebackService chargebackService;

    @GetMapping
    public List<Chargeback> getAllChargebacks() {
        return chargebackService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Chargeback> getChargeback(@PathVariable Long id) {
        return chargebackService.findById(id);
    }

    @PostMapping
    public Chargeback createChargeback(@RequestBody Chargeback chargeback) {
        return chargebackService.save(chargeback);
    }

    @PutMapping("/{id}")
    public Chargeback updateChargeback(@PathVariable Long id, @RequestBody Chargeback chargeback) {
        chargeback.setChargebackId(id);  // ID'yi set et
        return chargebackService.save(chargeback);
    }

    @DeleteMapping("/{id}")
    public void softDeleteChargeback(@PathVariable Long id) {
        chargebackService.softDelete(id, "system"); // "system" yerine kullanıcı bilgisi eklenebilir
    }
}
