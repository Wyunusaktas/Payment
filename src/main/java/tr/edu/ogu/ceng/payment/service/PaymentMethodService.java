package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.PaymentMethodDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;

    public List<PaymentMethodDTO> findAll() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentMethod -> modelMapper.map(paymentMethod, PaymentMethodDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PaymentMethodDTO> findById(Long id) {
        return paymentMethodRepository.findById(id)
                .map(paymentMethod -> modelMapper.map(paymentMethod, PaymentMethodDTO.class));
    }

    public PaymentMethodDTO save(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return modelMapper.map(savedPaymentMethod, PaymentMethodDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findById(id);
        if (paymentMethodOptional.isPresent()) {
            PaymentMethod paymentMethod = paymentMethodOptional.get();
            paymentMethod.setDeletedAt(java.time.LocalDateTime.now());
            paymentMethod.setDeletedBy(deletedBy);
            paymentMethodRepository.save(paymentMethod);
        }
    }
}
