package tr.edu.ogu.ceng.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.payment.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Belirli bir ödemeye ait işlemler
    List<Transaction> findByPaymentPaymentId(Long paymentId);

    // Belirli bir siparişe ait işlemler
    List<Transaction> findByOrderId(UUID orderId);

    // Durum bazlı işlem listesi
    List<Transaction> findByStatus(String status);

    // Tarih aralığına göre işlemler
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Belirli bir tutarın üzerindeki işlemler
    List<Transaction> findByAmountGreaterThan(BigDecimal amount);

    // Para birimi bazlı işlemler
    List<Transaction> findByCurrencyId(Long currencyId);

    // Karmaşık sorgu - JPQL
    @Query("SELECT t FROM Transaction t WHERE t.status = :status " +
            "AND t.amount BETWEEN :minAmount AND :maxAmount " +
            "AND t.transactionDate >= :startDate")
    List<Transaction> findTransactionsByStatusAmountRangeAndDate(
            @Param("status") String status,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate
    );

    // Native SQL örneği - İşlem özeti
    @Query(value = "SELECT DATE(transaction_date) as date, " +
            "COUNT(*) as count, SUM(amount) as total_amount " +
            "FROM transactions " +
            "WHERE status = 'COMPLETED' " +
            "GROUP BY DATE(transaction_date)",
            nativeQuery = true)
    List<Object[]> getTransactionSummaryByDate();

    // Belirli bir para birimindeki toplam işlem tutarı
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.currency.id = :currencyId AND t.status = 'COMPLETED'")
    BigDecimal calculateTotalAmountByCurrency(@Param("currencyId") Long currencyId);

    // Son N işlem
    List<Transaction> findTopNByOrderByTransactionDateDesc(int n);

    // Başarısız işlemleri bulma
    @Query("SELECT t FROM Transaction t WHERE t.status = 'FAILED' " +
            "AND t.transactionDate >= :startDate")
    List<Transaction> findRecentFailedTransactions(@Param("startDate") LocalDateTime startDate);

    // Ortalama işlem tutarının üzerindeki işlemler
    @Query("SELECT t FROM Transaction t WHERE t.amount > " +
            "(SELECT AVG(t2.amount) FROM Transaction t2)")
    List<Transaction> findAboveAverageTransactions();

    // Günlük işlem limiti kontrolü
    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.orderId = :orderId " +
            "AND DATE(t.transactionDate) = CURRENT_DATE")
    Long countDailyTransactionsByOrder(@Param("orderId") UUID orderId);
}