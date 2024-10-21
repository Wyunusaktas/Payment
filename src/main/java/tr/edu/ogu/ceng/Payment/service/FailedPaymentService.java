package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.FailedPayment;
import tr.edu.ogu.ceng.Payment.repository.FailedPaymentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FailedPaymentService {

    private final FailedPaymentRepository failedPaymentRepository;

    public List<FailedPayment> findAll() {
        return failedPaymentRepository.findAll();
    }

    public Optional<FailedPayment> findById(Long id) {
        return failedPaymentRepository.findById(id);
    }

    public FailedPayment save(FailedPayment failedPayment) {
        return failedPaymentRepository.save(failedPayment);
    }

    public void deleteById(Long id) {
        failedPaymentRepository.deleteById(id);
    }
}
