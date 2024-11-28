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

import tr.edu.ogu.ceng.payment.entity.FailedPayment;
import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@SpringBootTest
public class FailedPaymentRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private FailedPaymentRepository failedPaymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private FailedPayment failedPayment1;
    private FailedPayment failedPayment2;
    private PaymentMethod paymentMethod;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        paymentMethod = new PaymentMethod();
        paymentMethod.setUserId(userId);
        paymentMethod.setType("CREDIT_CARD");
        paymentMethod.setProvider("VISA");
        paymentMethod.setAccountNumber("4111111111111111");
        paymentMethod.setExpiryDate(LocalDateTime.now().plusYears(2).toLocalDate());
        paymentMethodRepository.save(paymentMethod);

        failedPayment1 = new FailedPayment();
        failedPayment1.setUserId(userId);
        failedPayment1.setPaymentMethod(paymentMethod);
        failedPayment1.setAmount(new BigDecimal("100.00"));
        failedPayment1.setFailureReason("Insufficient funds");
        failedPayment1.setAttemptDate(LocalDateTime.now().minusHours(2));
        failedPaymentRepository.save(failedPayment1);

        failedPayment2 = new FailedPayment();
        failedPayment2.setUserId(userId);
        failedPayment2.setPaymentMethod(paymentMethod);
        failedPayment2.setAmount(new BigDecimal("200.00"));
        failedPayment2.setFailureReason("Card declined");
        failedPayment2.setAttemptDate(LocalDateTime.now());
        failedPaymentRepository.save(failedPayment2);
    }

    @Test
    public void testFindById() {
        Optional<FailedPayment> found = failedPaymentRepository.findById(failedPayment1.getFailedPaymentId());

        assertThat(found).isPresent();
        assertThat(found.get().getFailureReason()).isEqualTo(failedPayment1.getFailureReason());
    }

    @Test
    public void testFindByUserId() {
        List<FailedPayment> failedPayments = failedPaymentRepository.findByUserId(userId);

        assertThat(failedPayments).hasSize(2);
        assertThat(failedPayments).allMatch(fp -> fp.getUserId().equals(userId));
    }

    @Test
    public void testFindByPaymentMethodMethodId() {
        List<FailedPayment> failedPayments = failedPaymentRepository.findByPaymentMethodMethodId(paymentMethod.getMethodId());

        assertThat(failedPayments).hasSize(2);
        assertThat(failedPayments).allMatch(fp -> fp.getPaymentMethod().equals(paymentMethod));
    }

    @Test
    public void testFindByAmountGreaterThan() {
        List<FailedPayment> largeFailedPayments = failedPaymentRepository.findByAmountGreaterThan(new BigDecimal("150.00"));

        assertThat(largeFailedPayments).hasSize(1);
        assertThat(largeFailedPayments.get(0).getAmount()).isGreaterThan(new BigDecimal("150.00"));
    }

    @Test
    public void testFindByAttemptDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusHours(3);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        List<FailedPayment> failedPayments = failedPaymentRepository.findByAttemptDateBetween(startDate, endDate);

        assertThat(failedPayments).hasSize(2);
    }

    @Test
    public void testFindByFailureReasonContaining() {
        List<FailedPayment> insufficientFunds = failedPaymentRepository.findByFailureReasonContaining("funds");
        List<FailedPayment> declinedCards = failedPaymentRepository.findByFailureReasonContaining("declined");

        assertThat(insufficientFunds).hasSize(1);
        assertThat(declinedCards).hasSize(1);
    }

    @Test
    public void testFindFirstByUserIdOrderByAttemptDateDesc() {
        Optional<FailedPayment> latestFailure = failedPaymentRepository.findFirstByUserIdOrderByAttemptDateDesc(userId);

        assertThat(latestFailure).isPresent();
        assertThat(latestFailure.get().getFailureReason()).isEqualTo(failedPayment2.getFailureReason());
    }

    @Test
    public void testFindByUserIdAndPaymentMethod() {
        List<FailedPayment> userMethodFailures = failedPaymentRepository.findByUserIdAndPaymentMethodMethodId(userId, paymentMethod.getMethodId());

        assertThat(userMethodFailures).hasSize(2);
        assertThat(userMethodFailures).allMatch(fp -> fp.getUserId().equals(userId) && fp.getPaymentMethod().equals(paymentMethod));
    }

    @Test
    public void testSoftDelete() {
        FailedPayment payment = failedPaymentRepository.findById(failedPayment1.getFailedPaymentId()).orElseThrow();
        payment.setDeletedAt(LocalDateTime.now());
        payment.setDeletedBy("testUser");
        failedPaymentRepository.save(payment);

        Optional<FailedPayment> deletedPayment = failedPaymentRepository.findById(failedPayment1.getFailedPaymentId());
        assertThat(deletedPayment).isEmpty();
    }

    @Test
    public void testUpdateFailureReason() {
        FailedPayment payment = failedPaymentRepository.findById(failedPayment1.getFailedPaymentId()).orElseThrow();
        String newReason = "Card expired";
        payment.setFailureReason(newReason);
        failedPaymentRepository.save(payment);

        FailedPayment updatedPayment = failedPaymentRepository.findById(failedPayment1.getFailedPaymentId()).orElseThrow();
        assertThat(updatedPayment.getFailureReason()).isEqualTo(newReason);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
