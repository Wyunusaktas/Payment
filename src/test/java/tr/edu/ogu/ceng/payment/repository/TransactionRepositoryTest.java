package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.Transaction;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.Currency;

@SpringBootTest
public class TransactionRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Transaction transaction1;
    private Transaction transaction2;
    private Payment payment;
    private Currency currency;
    private UUID orderId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        // Currency oluştur
        currency = new Currency();
        currency.setCurrencyName("USD");
        currency.setSymbol("$");
        currency.setExchangeRate(BigDecimal.ONE);
        currencyRepository.save(currency);

        // Payment oluştur
        payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency(currency);
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // Transaction1 oluştur
        transaction1 = new Transaction();
        transaction1.setPayment(payment);
        transaction1.setOrderId(orderId);
        transaction1.setStatus("COMPLETED");
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setCurrency(currency);
        transaction1.setTransactionDate(LocalDateTime.now().minusDays(1));
        transactionRepository.save(transaction1);

        // Transaction2 oluştur
        transaction2 = new Transaction();
        transaction2.setPayment(payment);
        transaction2.setOrderId(orderId);
        transaction2.setStatus("PENDING");
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setCurrency(currency);
        transaction2.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction2);
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<Transaction> transactions = transactionRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(transactions).hasSize(2);
        assertThat(transactions).allMatch(t -> t.getPayment().equals(payment));
    }

    @Test
    public void testFindByOrderId() {
        List<Transaction> transactions = transactionRepository.findByOrderId(orderId);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).allMatch(t -> t.getOrderId().equals(orderId));
    }

    @Test
    public void testFindByStatus() {
        List<Transaction> completedTransactions = transactionRepository.findByStatus("COMPLETED");

        assertThat(completedTransactions).hasSize(1);
        assertThat(completedTransactions.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    public void testFindByTransactionDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);

        assertThat(transactions).hasSize(2);
    }

    @Test
    public void testFindTransactionsByStatusAmountRangeAndDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        List<Transaction> transactions = transactionRepository.findTransactionsByStatusAmountRangeAndDate(
                "COMPLETED",
                new BigDecimal("50.00"),
                new BigDecimal("150.00"),
                startDate
        );

        assertThat(transactions).hasSize(1);
    }

    @Test
    public void testCalculateTotalAmountByCurrency() {
        BigDecimal total = transactionRepository.calculateTotalAmountByCurrency(currency.getId());

        assertThat(total).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
