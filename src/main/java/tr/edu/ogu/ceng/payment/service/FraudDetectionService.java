package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.FraudDetectionDTO;
import tr.edu.ogu.ceng.payment.entity.FraudDetection;
import tr.edu.ogu.ceng.payment.repository.FraudDetectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FraudDetectionService {

    private final FraudDetectionRepository fraudDetectionRepository;
    private final ModelMapper modelMapper;

    public List<FraudDetectionDTO> findAll() {
        return fraudDetectionRepository.findAll()
                .stream()
                .map(fraudDetection -> modelMapper.map(fraudDetection, FraudDetectionDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<FraudDetectionDTO> findById(Long id) {
        return fraudDetectionRepository.findById(id)
                .map(fraudDetection -> modelMapper.map(fraudDetection, FraudDetectionDTO.class));
    }

    public FraudDetectionDTO save(FraudDetectionDTO fraudDetectionDTO) {
        FraudDetection fraudDetection = modelMapper.map(fraudDetectionDTO, FraudDetection.class);
        FraudDetection savedFraudDetection = fraudDetectionRepository.save(fraudDetection);
        return modelMapper.map(savedFraudDetection, FraudDetectionDTO.class);
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
