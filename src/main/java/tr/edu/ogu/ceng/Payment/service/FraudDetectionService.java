package tr.edu.ogu.ceng.Payment.service;

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

    public void deleteById(Long id) {
        fraudDetectionRepository.deleteById(id);
    }
}
