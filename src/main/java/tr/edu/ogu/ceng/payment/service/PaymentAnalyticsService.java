package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.PaymentAnalyticsDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentAnalytics;
import tr.edu.ogu.ceng.payment.repository.PaymentAnalyticsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentAnalyticsService {

    private final PaymentAnalyticsRepository paymentAnalyticsRepository;
    private final ModelMapper modelMapper;

    public List<PaymentAnalyticsDTO> findAll() {
        return paymentAnalyticsRepository.findAll()
                .stream()
                .map(paymentAnalytics -> modelMapper.map(paymentAnalytics, PaymentAnalyticsDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PaymentAnalyticsDTO> findById(Long id) {
        return paymentAnalyticsRepository.findById(id)
                .map(paymentAnalytics -> modelMapper.map(paymentAnalytics, PaymentAnalyticsDTO.class));
    }

    public PaymentAnalyticsDTO save(PaymentAnalyticsDTO paymentAnalyticsDTO) {
        PaymentAnalytics paymentAnalytics = modelMapper.map(paymentAnalyticsDTO, PaymentAnalytics.class);
        PaymentAnalytics savedPaymentAnalytics = paymentAnalyticsRepository.save(paymentAnalytics);
        return modelMapper.map(savedPaymentAnalytics, PaymentAnalyticsDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<PaymentAnalytics> paymentAnalyticsOptional = paymentAnalyticsRepository.findById(id);
        if (paymentAnalyticsOptional.isPresent()) {
            PaymentAnalytics paymentAnalytics = paymentAnalyticsOptional.get();
            paymentAnalytics.setDeletedAt(java.time.LocalDateTime.now());
            paymentAnalytics.setDeletedBy(deletedBy);
            paymentAnalyticsRepository.save(paymentAnalytics);
        }
    }
}
