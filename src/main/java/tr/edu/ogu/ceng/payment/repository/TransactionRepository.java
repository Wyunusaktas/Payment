package tr.edu.ogu.ceng.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Belirli bir ödeme ID'sine ait işlemler
    List<Transaction> findByPayment_PaymentId(UUID paymentId);

    // İşlem durumuna göre filtrele
    List<Transaction> findByStatus(String status);

    // Belirli bir tarih aralığındaki işlemler
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Toplam işlem tutarını hesapla
    @Query("SELECT SUM(t.amount) FROM Transaction t")
    BigDecimal calculateTotalTransactionAmount();

    // Belirli bir ödeme ID'sine ait toplam işlem tutarını hesapla
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.payment.paymentId = :paymentId")
    BigDecimal calculateTotalAmountByPaymentId(UUID paymentId);
}
