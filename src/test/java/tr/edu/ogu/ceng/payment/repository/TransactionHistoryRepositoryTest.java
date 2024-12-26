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

import tr.edu.ogu.ceng.payment.common.TestContainerConfig;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.TransactionHistory;

@SpringBootTest
@Testcontainers
public class TransactionHistoryRepositoryTest  extends  TestContainerConfig {

   
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @BeforeEach
    public void setUp() {
        // Clear the repositories before each test
        transactionHistoryRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    // Test: findByUserId
    @Test
    public void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        paymentRepository.save(payment);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId);
        transactionHistory.setPayment(payment);
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());
        transactionHistory.setStatus("COMPLETED");

        transactionHistoryRepository.save(transactionHistory);

        List<TransactionHistory> transactions = transactionHistoryRepository.findByUserId(userId);
        assertThat(transactions).hasSize(1); // Ensure we have 1 transaction for this user
    }

    // Test: findByPayment_PaymentId
    @Test
    public void testFindByPayment_PaymentId() {
        UUID userId = UUID.randomUUID(); 
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
    
        Payment savedPayment = paymentRepository.save(payment);
     
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId); 
        transactionHistory.setPayment(savedPayment);  // Use savedPayment with generated paymentId
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());
        transactionHistory.setStatus("COMPLETED");
     
        // Save transaction history after payment is saved
        transactionHistoryRepository.save(transactionHistory);
     
        // Fetch transactions by paymentId
        List<TransactionHistory> transactions = transactionHistoryRepository.findByPayment_PaymentId(savedPayment.getPaymentId());
        assertThat(transactions).hasSize(1); // Ensure we have 1 transaction for this payment
    }

    // Test: findByTransactionType
    @Test
    public void testFindByTransactionType() {
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
        Payment savedPayment = paymentRepository.save(payment);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId);
        transactionHistory.setPayment(payment);
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());
        transactionHistory.setStatus("COMPLETED");

        transactionHistoryRepository.save(transactionHistory);

        List<TransactionHistory> transactions = transactionHistoryRepository.findByTransactionType("WITHDRAWAL");
        assertThat(transactions).hasSize(1); // Ensure we have 1 transaction of type "WITHDRAWAL"
    }

    // Test: findByTransactionDateBetween
    @Test
    public void testFindByTransactionDateBetween() {
        // Define the date range to be a few minutes before and after
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(5);  // 5 minutes before now
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(5);     // 5 minutes after now
         
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());  // This is within the date range
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
         
        Payment savedPayment = paymentRepository.save(payment);
         
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId);
        transactionHistory.setPayment(savedPayment);  // Use savedPayment with valid paymentId
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now()); // This is also within the range
        transactionHistory.setStatus("COMPLETED");
         
        // Save transaction history
        transactionHistoryRepository.save(transactionHistory);
         
        // Fetch transactions within the date range
        List<TransactionHistory> transactions = transactionHistoryRepository.findByTransactionDateBetween(startDate, endDate);
        
        // Assert that there is exactly one transaction within the date range
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(transactions.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    // Test: calculateTotalAmountByUserId
    @Test
    public void testCalculateTotalAmountByUserId() {
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        paymentRepository.save(payment);

        TransactionHistory transactionHistory1 = new TransactionHistory();
        transactionHistory1.setUserId(userId);
        transactionHistory1.setPayment(payment);
        transactionHistory1.setTransactionType("WITHDRAWAL");
        transactionHistory1.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory1.setTransactionDate(LocalDateTime.now());
        transactionHistory1.setStatus("COMPLETED");

        TransactionHistory transactionHistory2 = new TransactionHistory();
        transactionHistory2.setUserId(userId);
        transactionHistory2.setPayment(payment);
        transactionHistory2.setTransactionType("WITHDRAWAL");
        transactionHistory2.setAmount(BigDecimal.valueOf(30.00));
        transactionHistory2.setTransactionDate(LocalDateTime.now());
        transactionHistory2.setStatus("COMPLETED");

        transactionHistoryRepository.save(transactionHistory1);
        transactionHistoryRepository.save(transactionHistory2);

        BigDecimal totalAmount = transactionHistoryRepository.calculateTotalAmountByUserId(userId);
        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.valueOf(80.00)); // Total should be 80.00
    }
}
