package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.ThirdPartyPayment;
import tr.edu.ogu.ceng.Payment.repository.ThirdPartyPaymentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ThirdPartyPaymentService {

    private final ThirdPartyPaymentRepository thirdPartyPaymentRepository;

    public List<ThirdPartyPayment> findAll() {
        return thirdPartyPaymentRepository.findAll();
    }

    public Optional<ThirdPartyPayment> findById(Long id) {
        return thirdPartyPaymentRepository.findById(id);
    }

    public ThirdPartyPayment save(ThirdPartyPayment thirdPartyPayment) {
        return thirdPartyPaymentRepository.save(thirdPartyPayment);
    }

    public void deleteById(Long id) {
        thirdPartyPaymentRepository.deleteById(id);
    }
}
