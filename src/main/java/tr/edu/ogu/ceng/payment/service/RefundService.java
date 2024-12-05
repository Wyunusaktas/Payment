package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.repository.RefundRepository;

@Service
public class RefundService {

    private final RefundRepository refundRepository;

    @Autowired
    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    // Belirli bir ödeme ID'sine ait iadeleri getirir
    public List<Refund> getRefundsByPaymentId(UUID paymentId) {
        return refundRepository.findByPayment_PaymentId(paymentId);
    }

    // İade durumuna göre iadeleri getirir
    public List<Refund> getRefundsByStatus(String status) {
        return refundRepository.findByStatus(status);
    }

    // Belirli bir tarih aralığındaki iadeleri getirir
    public List<Refund> getRefundsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return refundRepository.findByRefundDateBetween(startDate, endDate);
    }

    // Tüm iadelerin toplam tutarını hesaplar
    public BigDecimal calculateTotalRefundAmount() {
        return refundRepository.calculateTotalRefundAmount();
    }

    // Belirli bir durumdaki iadelerin toplam tutarını hesaplar
    public BigDecimal calculateTotalRefundAmountByStatus(String status) {
        return refundRepository.calculateTotalRefundAmountByStatus(status);
    }

    // Yeni iade kaydı ekler
    public Refund addRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    // İade kaydını günceller
    public Refund updateRefund(Refund refund) {
        if (refundRepository.existsById(refund.getRefundId())) {
            return refundRepository.save(refund);
        }
        throw new IllegalArgumentException("İade bulunamadı.");
    }

    // İade kaydını siler
    public void deleteRefund(UUID refundId) {
        if (refundRepository.existsById(refundId)) {
            refundRepository.deleteById(refundId);
        } else {
            throw new IllegalArgumentException("İade bulunamadı.");
        }
    }
}
