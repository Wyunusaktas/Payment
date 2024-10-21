package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Chargeback;
import tr.edu.ogu.ceng.Payment.repository.ChargebackRepository;

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

    public void deleteById(Long id) {
        chargebackRepository.deleteById(id);
    }
}
