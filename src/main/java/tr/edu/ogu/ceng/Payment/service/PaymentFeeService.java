package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.PaymentFee;
import tr.edu.ogu.ceng.Payment.repository.PaymentFeeRepository;

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

    public void deleteById(Long id) {
        paymentFeeRepository.deleteById(id);
    }
}
