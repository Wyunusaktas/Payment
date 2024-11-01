package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.FraudDetectionDTO;
import tr.edu.ogu.ceng.payment.service.FraudDetectionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fraud-detection")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping
    public List<FraudDetectionDTO> getAllFraudDetections() {
        return fraudDetectionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<FraudDetectionDTO> getFraudDetection(@PathVariable Long id) {
        return fraudDetectionService.findById(id);
    }

    @PostMapping
    public FraudDetectionDTO createFraudDetection(@RequestBody FraudDetectionDTO fraudDetectionDTO) {
        return fraudDetectionService.save(fraudDetectionDTO);
    }

    @PutMapping("/{id}")
    public FraudDetectionDTO updateFraudDetection(@PathVariable Long id, @RequestBody FraudDetectionDTO fraudDetectionDTO) {
        fraudDetectionDTO.setFraudCaseId(id);  // ID'yi ayarla
        return fraudDetectionService.save(fraudDetectionDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteFraudDetection(@PathVariable Long id) {
        fraudDetectionService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
