package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Kullanıcıya ait tüm ödemeleri getirir
    public List<Payment> getAllPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    // Ödeme durumuna göre ödemeleri getirir
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    // PaymentMethod ile filtreleme (methodId)
    public List<Payment> getPaymentsByPaymentMethod(UUID methodId) {
        return paymentRepository.findByPaymentMethod_MethodId(methodId);
    }

    // Belirli bir tarih aralığındaki ödemeleri getirir
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByTransactionDateBetween(startDate, endDate);
    }

    // Kullanıcıya ait belirli bir tarih aralığındaki ödemeleri getirir
    public List<Payment> getPaymentsByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    // Tüm ödemelerin toplam tutarını hesaplar
    public BigDecimal calculateTotalAmount() {
        return paymentRepository.calculateTotalAmount();
    }

    // Belirli bir durumdaki ödemelerin toplam tutarını hesaplar
    public BigDecimal calculateTotalAmountByStatus(String status) {
        return paymentRepository.calculateTotalAmountByStatus(status);
    }

    // Yeni ödeme kaydı ekler
    public Payment addPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Ödeme kaydını günceller
    public Payment updatePayment(Payment payment) {
        if (paymentRepository.existsById(payment.getPaymentId())) {
            return paymentRepository.save(payment);
        }
        throw new IllegalArgumentException("Ödeme bulunamadı.");
    }

    // Ödeme kaydını siler
    public void deletePayment(UUID paymentId) {
        if (paymentRepository.existsById(paymentId)) {
            paymentRepository.deleteById(paymentId);
        } else {
            throw new IllegalArgumentException("Ödeme bulunamadı.");
        }
    }
}
