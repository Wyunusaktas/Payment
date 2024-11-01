package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.FailedPaymentDTO;
import tr.edu.ogu.ceng.payment.entity.FailedPayment;
import tr.edu.ogu.ceng.payment.repository.FailedPaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FailedPaymentService {

    private final FailedPaymentRepository failedPaymentRepository;
    private final ModelMapper modelMapper;

    public List<FailedPaymentDTO> findAll() {
        return failedPaymentRepository.findAll()
                .stream()
                .map(failedPayment -> modelMapper.map(failedPayment, FailedPaymentDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<FailedPaymentDTO> findById(Long id) {
        return failedPaymentRepository.findById(id)
                .map(failedPayment -> modelMapper.map(failedPayment, FailedPaymentDTO.class));
    }

    public FailedPaymentDTO save(FailedPaymentDTO failedPaymentDTO) {
        FailedPayment failedPayment = modelMapper.map(failedPaymentDTO, FailedPayment.class);
        FailedPayment savedFailedPayment = failedPaymentRepository.save(failedPayment);
        return modelMapper.map(savedFailedPayment, FailedPaymentDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<FailedPayment> failedPaymentOptional = failedPaymentRepository.findById(id);
        if (failedPaymentOptional.isPresent()) {
            FailedPayment failedPayment = failedPaymentOptional.get();
            failedPayment.setDeletedAt(java.time.LocalDateTime.now());
            failedPayment.setDeletedBy(deletedBy);
            failedPaymentRepository.save(failedPayment);
        }
    }
}
