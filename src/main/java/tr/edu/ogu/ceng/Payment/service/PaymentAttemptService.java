package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.PaymentAttempt;
import tr.edu.ogu.ceng.Payment.repository.PaymentAttemptRepository;

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

    public void deleteById(Long id) {
        paymentAttemptRepository.deleteById(id);
    }
}
