package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.Payment.model.PaymentMethod;
import tr.edu.ogu.ceng.Payment.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }

    public Optional<PaymentMethod> findById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    public PaymentMethod save(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findById(id);
        if (paymentMethodOptional.isPresent()) {
            PaymentMethod paymentMethod = paymentMethodOptional.get();
            paymentMethod.setDeletedAt(java.time.LocalDateTime.now());
            paymentMethod.setDeletedBy(deletedBy);
            paymentMethodRepository.save(paymentMethod);
        }
    }
}
