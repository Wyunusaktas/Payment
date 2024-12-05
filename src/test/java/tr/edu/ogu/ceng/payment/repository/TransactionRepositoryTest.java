package tr.edu.ogu.ceng.payment.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.Transaction;

@SpringBootTest
@Testcontainers
public class TransactionRepositoryTest {

    // PostgreSQL container'ını başlatıyoruz
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // Test konteyneri başlatılır
    static {
        postgreSQLContainer.start();
    }

    // Veritabanı bağlantı bilgilerini dinamik olarak ekliyoruz
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    // Testten önce veritabanı nesneleri her testte oluşturulacak, setUp boş bırakıldı
    @BeforeEach
    public void setUp() {
    }

    // Test: findByPayment_PaymentId
    @Test
    public void testFindByPayment_PaymentId() {
        // Payment nesnelerini oluşturup kaydediyoruz
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);

        // Transaction nesnelerini oluşturup kaydediyoruz
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setPayment(payment);
        transaction1.setAmount(BigDecimal.valueOf(50.00));
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);

        // Payment1 için tüm işlemleri alıyoruz
        List<Transaction> transactions = transactionRepository.findByPayment_PaymentId(payment.getPaymentId());
        assertThat(transactions).hasSize(1); // payment1 ile ilgili 1 işlem var
    }

    // Test: findByStatus
    @Test
    public void testFindByStatus() {
        // Clean the repository before the test to avoid unexpected data
        transactionRepository.deleteAll(); 
        paymentRepository.deleteAll();
    
        // "COMPLETED" olan işlemleri sorguluyoruz
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
    
        Payment savedPayment = paymentRepository.save(payment);
    
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setPayment(payment);
        transaction1.setAmount(BigDecimal.valueOf(50.00));
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);
    
        // "COMPLETED" olan işlemleri sorguluyoruz
        List<Transaction> completedTransactions = transactionRepository.findByStatus("COMPLETED");
        assertThat(completedTransactions).hasSize(1); // 1 işlem "COMPLETED" durumunda
    }

    // Test: findByTransactionDateBetween
    @Test
    public void testFindByTransactionDateBetween() {
        // Clear the repositories before the test to avoid unexpected data
        transactionRepository.deleteAll(); 
        paymentRepository.deleteAll();
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now();
        
        // Set a fixed transaction date within the date range for clarity
        LocalDateTime fixedTransactionDate = LocalDateTime.of(2024, 12, 1, 12, 0, 0, 0); // A fixed date for the transaction
    
        // Create Payment and Transaction objects
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(fixedTransactionDate); // Use fixed date
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
    
        Payment savedPayment = paymentRepository.save(payment);
        
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setPayment(payment);
        transaction1.setAmount(BigDecimal.valueOf(50.00));
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(fixedTransactionDate); // Use fixed date
        transactionRepository.save(transaction1);
    
        // Log the saved transaction's date for debugging purposes
        System.out.println("Transaction saved with date: " + fixedTransactionDate);
    
        // Retrieve transactions within the specified date range
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        System.out.println("Transactions found: " + transactions.size());
    
        // Verify that the transaction is within the range
        assertThat(transactions).hasSize(1); // 1 transaction should be within this date range
    }

    // Test: calculateTotalTransactionAmount
    @Test
    public void testCalculateTotalTransactionAmount() {
        // Yeni Payment ve Transaction nesneleri oluşturuluyor
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setPayment(payment);
        transaction1.setAmount(BigDecimal.valueOf(50.00));
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);

        // Toplam işlem tutarını hesaplıyoruz
        BigDecimal totalAmount = transactionRepository.calculateTotalTransactionAmount();
        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.valueOf(50.00)); // 50.00 olmalı
    }

    // Test: calculateTotalAmountByPaymentId
    @Test
    public void testCalculateTotalAmountByPaymentId() {
        // Yeni Payment ve Transaction nesneleri oluşturuluyor
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setPayment(payment);
        transaction1.setAmount(BigDecimal.valueOf(50.00));
        transaction1.setStatus("COMPLETED");
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);

        // Payment1 ID'sine ait toplam işlem tutarını hesaplıyoruz
        BigDecimal totalAmountByPaymentId = transactionRepository.calculateTotalAmountByPaymentId(payment.getPaymentId());
        assertThat(totalAmountByPaymentId).isEqualByComparingTo(BigDecimal.valueOf(50.00)); // 50.00 olmalı
    }
}
