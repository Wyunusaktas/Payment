package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Kullanıcının ödemeleri
    List<Payment> findByUserIdOrderByTransactionDateDesc(UUID userId);

    // Belirli bir durumda olan ödemeler
    List<Payment> findByStatus(String status);

    // Belirli bir tarih aralığındaki ödemeler
    List<Payment> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Belirli bir tutardan yüksek ödemeler
    List<Payment> findByAmountGreaterThan(BigDecimal amount);

    // Belirli bir ödeme yöntemiyle yapılan ödemeler
    List<Payment> findByPaymentMethodMethodId(Long paymentMethodId);

    // Belirli bir para birimiyle yapılan ödemeler
    List<Payment> findByCurrencyId(Long currencyId);

    // Tekrarlayan ödemeler
    List<Payment> findByRecurringTrue();

    // Belirli bir kanaldan yapılan ödemeler
    List<Payment> findByPaymentChannel(String channel);

    // İndirim uygulanmış ödemeler
    List<Payment> findByDiscountAppliedGreaterThan(BigDecimal zero);

    // Karmaşık sorgu örneği - JPQL
    @Query("SELECT p FROM Payment p WHERE p.status = :status " +
            "AND p.amount > :minAmount " +
            "AND p.transactionDate >= :startDate")
    List<Payment> findPaymentsByStatusAmountAndDate(
            @Param("status") String status,
            @Param("minAmount") BigDecimal minAmount,
            @Param("startDate") LocalDateTime startDate
    );

    // Native SQL sorgu örneği
    @Query(value = "SELECT * FROM payments p " +
            "INNER JOIN payment_methods pm ON p.payment_method = pm.method_id " +
            "WHERE pm.provider = :provider " +
            "AND p.status = 'COMPLETED'",
            nativeQuery = true)
    List<Payment> findCompletedPaymentsByProvider(@Param("provider") String provider);

    // Belirli bir kullanıcının toplam ödeme tutarı
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userId = :userId")
    BigDecimal calculateTotalPaymentAmountByUser(@Param("userId") UUID userId);

    // Kullanıcının son ödemesi
    Optional<Payment> findFirstByUserIdOrderByTransactionDateDesc(UUID userId);

    // Belirli bir dönemdeki başarılı ödemeler
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' " +
            "AND p.transactionDate BETWEEN :startDate AND :endDate")
    List<Payment> findSuccessfulPaymentsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Yüksek tutarlı ödemeleri bulma
    @Query("SELECT p FROM Payment p WHERE p.amount > " +
            "(SELECT AVG(p2.amount) * 2 FROM Payment p2)")
    List<Payment> findHighValuePayments();
}