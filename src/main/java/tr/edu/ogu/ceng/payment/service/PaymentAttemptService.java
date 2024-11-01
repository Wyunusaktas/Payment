package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.PaymentAttemptDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentAttempt;
import tr.edu.ogu.ceng.payment.repository.PaymentAttemptRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentAttemptService {

    private final PaymentAttemptRepository paymentAttemptRepository;
    private final ModelMapper modelMapper;

    public List<PaymentAttemptDTO> findAll() {
        return paymentAttemptRepository.findAll()
                .stream()
                .map(paymentAttempt -> modelMapper.map(paymentAttempt, PaymentAttemptDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PaymentAttemptDTO> findById(Long id) {
        return paymentAttemptRepository.findById(id)
                .map(paymentAttempt -> modelMapper.map(paymentAttempt, PaymentAttemptDTO.class));
    }

    public PaymentAttemptDTO save(PaymentAttemptDTO paymentAttemptDTO) {
        PaymentAttempt paymentAttempt = modelMapper.map(paymentAttemptDTO, PaymentAttempt.class);
        PaymentAttempt savedPaymentAttempt = paymentAttemptRepository.save(paymentAttempt);
        return modelMapper.map(savedPaymentAttempt, PaymentAttemptDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentAttempt> paymentAttemptOptional = paymentAttemptRepository.findById(id);
        if (paymentAttemptOptional.isPresent()) {
            PaymentAttempt paymentAttempt = paymentAttemptOptional.get();
            paymentAttempt.setDeletedAt(java.time.LocalDateTime.now());
            paymentAttempt.setDeletedBy(deletedBy);
            paymentAttemptRepository.save(paymentAttempt);
        }
    }
}
