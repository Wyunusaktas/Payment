package tr.edu.ogu.ceng.Payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Discount;
import tr.edu.ogu.ceng.Payment.repository.DiscountRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    public Optional<Discount> findById(Long id) {
        return discountRepository.findById(id);
    }

    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Discount> discountOptional = discountRepository.findById(id);
        if (discountOptional.isPresent()) {
            Discount discount = discountOptional.get();
            discount.setDeletedAt(java.time.LocalDateTime.now());
            discount.setDeletedBy(deletedBy);
            discountRepository.save(discount);
        }
    }
}
