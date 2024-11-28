package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
import tr.edu.ogu.ceng.payment.entity.Payment;

@SpringBootTest
public class TransactionHistoryRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private TransactionHistory history1;
    private TransactionHistory history2;
    private Payment payment;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Payment oluştur
        payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);

        // İlk işlem geçmişi
        history1 = new TransactionHistory();
        history1.setUserId(userId);
        history1.setPayment(payment);
        history1.setTransactionType("PAYMENT");
        history1.setAmount(new BigDecimal("100.00"));
        history1.setTransactionDate(LocalDateTime.now().minusHours(2));
        history1.setStatus("COMPLETED");
        transactionHistoryRepository.save(history1);

        // İkinci işlem geçmişi
        history2 = new TransactionHistory();
        history2.setUserId(userId);
        history2.setPayment(payment);
        history2.setTransactionType("REFUND");
        history2.setAmount(new BigDecimal("50.00"));
        history2.setTransactionDate(LocalDateTime.now());
        history2.setStatus("PENDING");
        transactionHistoryRepository.save(history2);
    }

    @Test
    public void testFindById() {
        Optional<TransactionHistory> found = transactionHistoryRepository.findById(history1.getHistoryId());

        assertThat(found).isPresent();
        assertThat(found.get().getTransactionType()).isEqualTo(history1.getTransactionType());
    }

    @Test
    public void testFindByUserId() {
        List<TransactionHistory> histories = transactionHistoryRepository.findByUserId(userId);

        assertThat(histories).hasSize(2);
        assertThat(histories).allMatch(h -> h.getUserId().equals(userId));
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<TransactionHistory> histories = transactionHistoryRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(histories).hasSize(2);
    }

    @Test
    public void testFindByTransactionType() {
        List<TransactionHistory> payments = transactionHistoryRepository.findByTransactionType("PAYMENT");

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getTransactionType()).isEqualTo("PAYMENT");
    }

    @Test
    public void testFindByStatus() {
        List<TransactionHistory> completedTransactions = transactionHistoryRepository.findByStatus("COMPLETED");

        assertThat(completedTransactions).hasSize(1);
    }

    @Test
    public void testFindByTransactionDateBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<TransactionHistory> histories = transactionHistoryRepository.findByTransactionDateBetween(startTime, endTime);

        assertThat(histories).hasSize(2);
    }

    @Test
    public void testFindByAmountGreaterThan() {
        List<TransactionHistory> largeTransactions = transactionHistoryRepository.findByAmountGreaterThan(
                new BigDecimal("75.00")
        );

        assertThat(largeTransactions).hasSize(1);
        assertThat(largeTransactions.get(0).getAmount()).isGreaterThan(new BigDecimal("75.00"));
    }

    @Test
    public void testFindFirstByUserIdOrderByTransactionDateDesc() {
        Optional<TransactionHistory> latestTransaction = transactionHistoryRepository
                .findFirstByUserIdOrderByTransactionDateDesc(userId);

        assertThat(latestTransaction).isPresent();
        assertThat(latestTransaction.get().getTransactionType()).isEqualTo(history2.getTransactionType());
    }

    @Test
    public void testFindByTransactionTypeAndStatus() {
        List<TransactionHistory> completedPayments = transactionHistoryRepository
                .findByTransactionTypeAndStatus("PAYMENT", "COMPLETED");

        assertThat(completedPayments).hasSize(1);
    }

    @Test
    public void testSoftDelete() {
        TransactionHistory history = transactionHistoryRepository.findById(history1.getHistoryId()).orElseThrow();
        history.setDeletedAt(LocalDateTime.now());
        history.setDeletedBy("testUser");
        transactionHistoryRepository.save(history);

        Optional<TransactionHistory> deletedHistory = transactionHistoryRepository.findById(history1.getHistoryId());
        assertThat(deletedHistory).isEmpty();
    }

    @Test
    public void testUpdateTransactionStatus() {
        TransactionHistory history = transactionHistoryRepository.findById(history2.getHistoryId()).orElseThrow();
        history.setStatus("COMPLETED");
        transactionHistoryRepository.save(history);

        TransactionHistory updatedHistory = transactionHistoryRepository.findById(history2.getHistoryId()).orElseThrow();
        assertThat(updatedHistory.getStatus()).isEqualTo("COMPLETED");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
