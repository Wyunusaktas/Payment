package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.model.Chargeback;
import tr.edu.ogu.ceng.payment.repository.ChargebackRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChargebackService {

    private final ChargebackRepository chargebackRepository;

    public List<Chargeback> findAll() {
        return chargebackRepository.findAll();
    }

    public Optional<Chargeback> findById(Long id) {
        return chargebackRepository.findById(id);
    }

    public Chargeback save(Chargeback chargeback) {
        return chargebackRepository.save(chargeback);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Chargeback> chargebackOptional = chargebackRepository.findById(id);
        if (chargebackOptional.isPresent()) {
            Chargeback chargeback = chargebackOptional.get();
            chargeback.setDeletedAt(java.time.LocalDateTime.now());
            chargeback.setDeletedBy(deletedBy);
            chargebackRepository.save(chargeback);
        }
    }
}
