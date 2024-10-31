package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.model.FraudDetection;
import tr.edu.ogu.ceng.payment.service.FraudDetectionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fraud-detection")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping
    public List<FraudDetection> getAllFraudDetections() {
        return fraudDetectionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<FraudDetection> getFraudDetection(@PathVariable Long id) {
        return fraudDetectionService.findById(id);
    }

    @PostMapping
    public FraudDetection createFraudDetection(@RequestBody FraudDetection fraudDetection) {
        return fraudDetectionService.save(fraudDetection);
    }

    @PutMapping("/{id}")
    public FraudDetection updateFraudDetection(@PathVariable Long id, @RequestBody FraudDetection fraudDetection) {
        fraudDetection.setFraudCaseId(id);  // ID'yi set et
        return fraudDetectionService.save(fraudDetection);
    }

    @DeleteMapping("/{id}")
    public void softDeleteFraudDetection(@PathVariable Long id) {
        fraudDetectionService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
