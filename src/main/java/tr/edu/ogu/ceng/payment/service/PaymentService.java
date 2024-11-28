package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.PaymentDTO;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    // Mevcut metodlar korundu
    // ...

    // Yeni metodlar eklendi
    public List<PaymentDTO> findPaymentsByUser(UUID userId) {
        return paymentRepository.findByUserIdOrderByTransactionDateDesc(userId)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByTransactionDateBetween(startDate, endDate)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findPaymentsAboveAmount(BigDecimal amount) {
        return paymentRepository.findByAmountGreaterThan(amount)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findPaymentsByMethod(Long paymentMethodId) {
        return paymentRepository.findByPaymentMethodMethodId(paymentMethodId)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findRecurringPayments() {
        return paymentRepository.findByRecurringTrue()
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findDiscountedPayments() {
        return paymentRepository.findByDiscountAppliedGreaterThan(BigDecimal.ZERO)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findPaymentsByStatusAmountAndDate(
            String status, BigDecimal minAmount, LocalDateTime startDate) {
        return paymentRepository.findPaymentsByStatusAmountAndDate(status, minAmount, startDate)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findCompletedPaymentsByProvider(String provider) {
        return paymentRepository.findCompletedPaymentsByProvider(provider)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPaymentAmountByUser(UUID userId) {
        return paymentRepository.calculateTotalPaymentAmountByUser(userId);
    }

    public Optional<PaymentDTO> findLastPaymentByUser(UUID userId) {
        return paymentRepository.findFirstByUserIdOrderByTransactionDateDesc(userId)
                .map(payment -> modelMapper.map(payment, PaymentDTO.class));
    }

    public List<PaymentDTO> findSuccessfulPaymentsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findSuccessfulPaymentsBetweenDates(startDate, endDate)
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findHighValuePayments() {
        return paymentRepository.findHighValuePayments()
                .stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }
}