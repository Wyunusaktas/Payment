package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.model.PaymentFee;
import tr.edu.ogu.ceng.payment.repository.PaymentFeeRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentFeeService {

    private final PaymentFeeRepository paymentFeeRepository;

    public List<PaymentFee> findAll() {
        return paymentFeeRepository.findAll();
    }

    public Optional<PaymentFee> findById(Long id) {
        return paymentFeeRepository.findById(id);
    }

    public PaymentFee save(PaymentFee paymentFee) {
        return paymentFeeRepository.save(paymentFee);
    }

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentFee> paymentFeeOptional = paymentFeeRepository.findById(id);
        if (paymentFeeOptional.isPresent()) {
            PaymentFee paymentFee = paymentFeeOptional.get();
            paymentFee.setDeletedAt(java.time.LocalDateTime.now());
            paymentFee.setDeletedBy(deletedBy);
            paymentFeeRepository.save(paymentFee);
        }
    }
}
