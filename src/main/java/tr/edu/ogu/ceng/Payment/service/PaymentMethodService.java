package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public void deleteById(Long id) {
        paymentMethodRepository.deleteById(id);
    }
}
