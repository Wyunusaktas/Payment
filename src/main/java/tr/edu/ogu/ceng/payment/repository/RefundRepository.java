package tr.edu.ogu.ceng.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.Refund;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    // Belirli bir ödeme ID'sine ait iadeler
    List<Refund> findByPayment_PaymentId(UUID paymentId);

    // İade durumuna göre filtrele
    List<Refund> findByStatus(String status);

    // Belirli bir tarih aralığındaki iadeler
    List<Refund> findByRefundDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Toplam iade tutarını hesapla
    @Query("SELECT SUM(r.refundAmount) FROM Refund r")
    BigDecimal calculateTotalRefundAmount();

    // Belirli bir durumdaki toplam iade tutarını hesapla
    @Query("SELECT SUM(r.refundAmount) FROM Refund r WHERE r.status = :status")
    BigDecimal calculateTotalRefundAmountByStatus(String status);
}
