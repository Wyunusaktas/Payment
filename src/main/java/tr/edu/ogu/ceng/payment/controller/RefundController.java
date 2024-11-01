package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.RefundDTO;
import tr.edu.ogu.ceng.payment.service.RefundService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refund")
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public List<RefundDTO> getAllRefunds() {
        return refundService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<RefundDTO> getRefund(@PathVariable Long id) {
        return refundService.findById(id);
    }

    @PostMapping
    public RefundDTO createRefund(@RequestBody RefundDTO refundDTO) {
        return refundService.save(refundDTO);
    }

    @PutMapping("/{id}")
    public RefundDTO updateRefund(@PathVariable Long id, @RequestBody RefundDTO refundDTO) {
        refundDTO.setRefundId(id);  // ID'yi ayarla
        return refundService.save(refundDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteRefund(@PathVariable Long id) {
        refundService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
