package tr.edu.ogu.ceng.Payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.FraudDetection;
import tr.edu.ogu.ceng.Payment.repository.FraudDetectionRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FraudDetectionService {

    private final FraudDetectionRepository fraudDetectionRepository;

    public List<FraudDetection> findAll() {
        return fraudDetectionRepository.findAll();
    }

    public Optional<FraudDetection> findById(Long id) {
        return fraudDetectionRepository.findById(id);
    }

    public FraudDetection save(FraudDetection fraudDetection) {
        return fraudDetectionRepository.save(fraudDetection);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<FraudDetection> fraudDetectionOptional = fraudDetectionRepository.findById(id);
        if (fraudDetectionOptional.isPresent()) {
            FraudDetection fraudDetection = fraudDetectionOptional.get();
            fraudDetection.setDeletedAt(java.time.LocalDateTime.now());
            fraudDetection.setDeletedBy(deletedBy);
            fraudDetectionRepository.save(fraudDetection);
        }
    }
}
