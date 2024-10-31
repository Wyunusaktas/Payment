package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.model.ThirdPartyPayment;
import tr.edu.ogu.ceng.payment.repository.ThirdPartyPaymentRepository;

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

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<ThirdPartyPayment> thirdPartyPaymentOptional = thirdPartyPaymentRepository.findById(id);
        if (thirdPartyPaymentOptional.isPresent()) {
            ThirdPartyPayment thirdPartyPayment = thirdPartyPaymentOptional.get();
            thirdPartyPayment.setDeletedAt(java.time.LocalDateTime.now());
            thirdPartyPayment.setDeletedBy(deletedBy);
            thirdPartyPaymentRepository.save(thirdPartyPayment);
        }
    }
}
