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

import tr.edu.ogu.ceng.payment.entity.Currency;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@SpringBootTest
public class PaymentRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Payment payment1;
    private Payment payment2;
    private PaymentMethod paymentMethod;
    private Currency currency;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        currency = new Currency();
        currency.setCurrencyName("USD");
        currency.setSymbol("$");
        currency.setExchangeRate(BigDecimal.ONE);
        currencyRepository.save(currency);

        paymentMethod = new PaymentMethod();
        paymentMethod.setType("CREDIT_CARD");
        paymentMethod.setProvider("VISA");
        paymentMethod.setUserId(userId);
        paymentMethod.setAccountNumber("4111111111111111");
        paymentMethod.setExpiryDate(LocalDateTime.now().plusYears(2).toLocalDate());
        paymentMethodRepository.save(paymentMethod);

        payment1 = new Payment();
        payment1.setUserId(userId);
        payment1.setAmount(new BigDecimal("100.00"));
        payment1.setCurrency(currency);
        payment1.setStatus("COMPLETED");
        payment1.setPaymentMethod(paymentMethod);
        payment1.setTransactionDate(LocalDateTime.now().minusDays(1));
        payment1.setRecurring(false);
        paymentRepository.save(payment1);

        payment2 = new Payment();
        payment2.setUserId(userId);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setCurrency(currency);
        payment2.setStatus("PENDING");
        payment2.setPaymentMethod(paymentMethod);
        payment2.setTransactionDate(LocalDateTime.now());
        payment2.setRecurring(true);
        paymentRepository.save(payment2);
    }

    @Test
    public void testFindByUserIdOrderByTransactionDateDesc() {
        List<Payment> payments = paymentRepository.findByUserIdOrderByTransactionDateDesc(userId);

        assertThat(payments).hasSize(2);
        assertThat(payments.get(0).getTransactionDate())
                .isAfterOrEqualTo(payments.get(1).getTransactionDate());
    }

    @Test
    public void testFindByStatus() {
        List<Payment> completedPayments = paymentRepository.findByStatus("COMPLETED");
        List<Payment> pendingPayments = paymentRepository.findByStatus("PENDING");

        assertThat(completedPayments).hasSize(1);
        assertThat(pendingPayments).hasSize(1);
    }

    @Test
    public void testFindByTransactionDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Payment> payments = paymentRepository.findByTransactionDateBetween(startDate, endDate);

        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByAmountGreaterThan() {
        List<Payment> payments = paymentRepository.findByAmountGreaterThan(new BigDecimal("150.00"));

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getAmount()).isGreaterThan(new BigDecimal("150.00"));
    }

    @Test
    public void testFindByPaymentMethodMethodId() {
        List<Payment> payments = paymentRepository.findByPaymentMethodMethodId(paymentMethod.getMethodId());

        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByCurrencyId() {
        List<Payment> payments = paymentRepository.findByCurrencyId(currency.getId());

        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByRecurringTrue() {
        List<Payment> recurringPayments = paymentRepository.findByRecurringTrue();

        assertThat(recurringPayments).hasSize(1);
        assertThat(recurringPayments.get(0).isRecurring()).isTrue();
    }

    @Test
    public void testFindPaymentsByStatusAmountAndDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        List<Payment> payments = paymentRepository.findPaymentsByStatusAmountAndDate(
                "COMPLETED",
                new BigDecimal("50.00"),
                startDate
        );

        assertThat(payments).hasSize(1);
    }

    @Test
    public void testCalculateTotalPaymentAmountByUser() {
        BigDecimal total = paymentRepository.calculateTotalPaymentAmountByUser(userId);

        assertThat(total).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    public void testFindFirstByUserIdOrderByTransactionDateDesc() {
        Payment lastPayment = paymentRepository.findFirstByUserIdOrderByTransactionDateDesc(userId)
                .orElseThrow();

        assertThat(lastPayment.getTransactionDate()).isEqualTo(payment2.getTransactionDate());
    }

    @Test
    public void testFindSuccessfulPaymentsBetweenDates() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Payment> payments = paymentRepository.findSuccessfulPaymentsBetweenDates(startDate, endDate);

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    public void testFindHighValuePayments() {
        List<Payment> highValuePayments = paymentRepository.findHighValuePayments();

        assertThat(highValuePayments).hasSize(1);
        assertThat(highValuePayments.get(0).getAmount())
                .isGreaterThan(new BigDecimal("150.00"));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
