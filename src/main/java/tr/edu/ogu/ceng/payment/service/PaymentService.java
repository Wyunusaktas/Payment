package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.PaymentDTO;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PaymentDTO> findById(Long id) {
        return paymentRepository.findById(id)
                .map(payment -> modelMapper.map(payment, PaymentDTO.class));
    }

    public PaymentDTO save(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        Payment savedPayment = paymentRepository.save(payment);
        return modelMapper.map(savedPayment, PaymentDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setDeletedAt(java.time.LocalDateTime.now());
            payment.setDeletedBy(deletedBy);
            paymentRepository.save(payment);
        }
    }
}
