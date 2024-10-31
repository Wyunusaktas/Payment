package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.model.Refund;
import tr.edu.ogu.ceng.payment.repository.RefundRepository;

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

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Refund> refundOptional = refundRepository.findById(id);
        if (refundOptional.isPresent()) {
            Refund refund = refundOptional.get();
            refund.setDeletedAt(java.time.LocalDateTime.now());
            refund.setDeletedBy(deletedBy);
            refundRepository.save(refund);
        }
    }
}
