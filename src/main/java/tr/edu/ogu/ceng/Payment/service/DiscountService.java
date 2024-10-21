package tr.edu.ogu.ceng.Payment.service;

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

    public void deleteById(Long id) {
        discountRepository.deleteById(id);
    }
}
