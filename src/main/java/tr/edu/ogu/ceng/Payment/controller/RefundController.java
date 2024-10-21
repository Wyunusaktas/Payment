package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Refund;
import tr.edu.ogu.ceng.Payment.service.RefundService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refund")
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public List<Refund> getAllRefunds() {
        return refundService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Refund> getRefund(@PathVariable Long id) {
        return refundService.findById(id);
    }

    @PostMapping
    public Refund createRefund(@RequestBody Refund refund) {
        return refundService.save(refund);
    }

    @PutMapping("/{id}")
    public Refund updateRefund(@PathVariable Long id, @RequestBody Refund refund) {
        refund.setRefundId(id);  // ID'yi set et
        return refundService.save(refund);
    }

    @DeleteMapping("/{id}")
    public void deleteRefund(@PathVariable Long id) {
        refundService.deleteById(id);
    }
}
