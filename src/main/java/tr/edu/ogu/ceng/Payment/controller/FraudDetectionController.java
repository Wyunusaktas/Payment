package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.FraudDetection;
import tr.edu.ogu.ceng.Payment.service.FraudDetectionService;

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
    public void deleteFraudDetection(@PathVariable Long id) {
        fraudDetectionService.deleteById(id);
    }
}
