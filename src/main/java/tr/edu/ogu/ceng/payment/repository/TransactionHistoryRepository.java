package tr.edu.ogu.ceng.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {

    // Kullanıcıya ait tüm işlem geçmişi
    List<TransactionHistory> findByUserId(UUID userId);

    // Belirli bir ödeme ID'sine ait işlem geçmişi
    List<TransactionHistory> findByPayment_PaymentId(UUID paymentId);

    // Belirli bir işlem türüne göre filtrele
    List<TransactionHistory> findByTransactionType(String transactionType);

    // Belirli bir tarih aralığındaki işlem geçmişi
    List<TransactionHistory> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Belirli bir kullanıcıya ait toplam işlem tutarını hesapla
    @Query("SELECT SUM(th.amount) FROM TransactionHistory th WHERE th.userId = :userId")
    BigDecimal calculateTotalAmountByUserId(UUID userId);
}
