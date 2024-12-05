package tr.edu.ogu.ceng.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Kullanıcıya ait tüm ödemeler
    List<Payment> findByUserId(UUID userId);

    // Ödeme durumuna göre filtrele
    List<Payment> findByStatus(String status);

    // PaymentMethod ile filtrele (methodId)
    List<Payment> findByPaymentMethod_MethodId(UUID methodId);

    // Belirli bir tarih aralığındaki ödemeler
    List<Payment> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Belirli bir kullanıcı ve tarih aralığındaki ödemeler
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.transactionDate BETWEEN :startDate AND :endDate")
    List<Payment> findByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    // Toplam ödeme tutarını hesapla
    @Query("SELECT SUM(p.amount) FROM Payment p")
    BigDecimal calculateTotalAmount();

    // Belirli bir durumdaki toplam ödeme tutarını hesapla
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal calculateTotalAmountByStatus(String status);
}
