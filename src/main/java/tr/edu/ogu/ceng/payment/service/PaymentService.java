package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.model.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setDeletedAt(java.time.LocalDateTime.now());
            payment.setDeletedBy(deletedBy);
            paymentRepository.save(payment);
        }
    }
}
