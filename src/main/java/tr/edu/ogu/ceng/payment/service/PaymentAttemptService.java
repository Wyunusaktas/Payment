package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.model.PaymentAttempt;
import tr.edu.ogu.ceng.payment.repository.PaymentAttemptRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentAttemptService {

    private final PaymentAttemptRepository paymentAttemptRepository;

    public List<PaymentAttempt> findAll() {
        return paymentAttemptRepository.findAll();
    }

    public Optional<PaymentAttempt> findById(Long id) {
        return paymentAttemptRepository.findById(id);
    }

    public PaymentAttempt save(PaymentAttempt paymentAttempt) {
        return paymentAttemptRepository.save(paymentAttempt);
    }

    // Soft delete işlemi için güncellenmiş metod
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
