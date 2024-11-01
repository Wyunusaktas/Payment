package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.PaymentFeeDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentFee;
import tr.edu.ogu.ceng.payment.repository.PaymentFeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentFeeService {

    private final PaymentFeeRepository paymentFeeRepository;
    private final ModelMapper modelMapper;

    public List<PaymentFeeDTO> findAll() {
        return paymentFeeRepository.findAll()
                .stream()
                .map(paymentFee -> modelMapper.map(paymentFee, PaymentFeeDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PaymentFeeDTO> findById(Long id) {
        return paymentFeeRepository.findById(id)
                .map(paymentFee -> modelMapper.map(paymentFee, PaymentFeeDTO.class));
    }

    public PaymentFeeDTO save(PaymentFeeDTO paymentFeeDTO) {
        PaymentFee paymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);
        PaymentFee savedPaymentFee = paymentFeeRepository.save(paymentFee);
        return modelMapper.map(savedPaymentFee, PaymentFeeDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentFee> paymentFeeOptional = paymentFeeRepository.findById(id);
        if (paymentFeeOptional.isPresent()) {
            PaymentFee paymentFee = paymentFeeOptional.get();
            paymentFee.setDeletedAt(java.time.LocalDateTime.now());
            paymentFee.setDeletedBy(deletedBy);
            paymentFeeRepository.save(paymentFee);
        }
    }
}
