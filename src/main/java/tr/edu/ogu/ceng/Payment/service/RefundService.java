package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Refund;
import tr.edu.ogu.ceng.Payment.repository.RefundRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefundService {

    private final RefundRepository refundRepository;

    public List<Refund> findAll() {
        return refundRepository.findAll();
    }

    public Optional<Refund> findById(Long id) {
        return refundRepository.findById(id);
    }

    public Refund save(Refund refund) {
        return refundRepository.save(refund);
    }

    public void deleteById(Long id) {
        refundRepository.deleteById(id);
    }
}
