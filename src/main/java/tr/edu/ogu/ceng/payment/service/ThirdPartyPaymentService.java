package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.ThirdPartyPaymentDTO;
import tr.edu.ogu.ceng.payment.entity.ThirdPartyPayment;
import tr.edu.ogu.ceng.payment.repository.ThirdPartyPaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ThirdPartyPaymentService {

    private final ThirdPartyPaymentRepository thirdPartyPaymentRepository;
    private final ModelMapper modelMapper;

    public List<ThirdPartyPaymentDTO> findAll() {
        return thirdPartyPaymentRepository.findAll()
                .stream()
                .map(payment -> modelMapper.map(payment, ThirdPartyPaymentDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<ThirdPartyPaymentDTO> findById(Long id) {
        return thirdPartyPaymentRepository.findById(id)
                .map(payment -> modelMapper.map(payment, ThirdPartyPaymentDTO.class));
    }

    public ThirdPartyPaymentDTO save(ThirdPartyPaymentDTO thirdPartyPaymentDTO) {
        ThirdPartyPayment payment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);
        ThirdPartyPayment savedPayment = thirdPartyPaymentRepository.save(payment);
        return modelMapper.map(savedPayment, ThirdPartyPaymentDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<ThirdPartyPayment> paymentOptional = thirdPartyPaymentRepository.findById(id);
        if (paymentOptional.isPresent()) {
            ThirdPartyPayment payment = paymentOptional.get();
            payment.setDeletedAt(java.time.LocalDateTime.now());
            payment.setDeletedBy(deletedBy);
            thirdPartyPaymentRepository.save(payment);
        }
    }
}
