package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.DiscountDTO;
import tr.edu.ogu.ceng.payment.entity.Discount;
import tr.edu.ogu.ceng.payment.repository.DiscountRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;

    public List<DiscountDTO> findAll() {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream()
                .map(discount -> modelMapper.map(discount, DiscountDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<DiscountDTO> findById(Long id) {
        return discountRepository.findById(id)
                .map(discount -> modelMapper.map(discount, DiscountDTO.class));
    }

    public DiscountDTO save(DiscountDTO discountDTO) {
        Discount discount = modelMapper.map(discountDTO, Discount.class);
        Discount savedDiscount = discountRepository.save(discount);
        return modelMapper.map(savedDiscount, DiscountDTO.class);
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
