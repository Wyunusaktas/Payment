package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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

import tr.edu.ogu.ceng.payment.entity.PaymentAttempt;
import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@SpringBootTest
public class PaymentAttemptRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentAttemptRepository paymentAttemptRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private PaymentAttempt attempt1;
    private PaymentAttempt attempt2;
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

        attempt1 = new PaymentAttempt();
        attempt1.setUserId(userId);
        attempt1.setPaymentMethod(paymentMethod);
        attempt1.setAmount(new BigDecimal("100.00"));
        attempt1.setAttemptStatus("SUCCESS");
        attempt1.setAttemptDate(LocalDateTime.now().minusHours(2));
        paymentAttemptRepository.save(attempt1);

        attempt2 = new PaymentAttempt();
        attempt2.setUserId(userId);
        attempt2.setPaymentMethod(paymentMethod);
        attempt2.setAmount(new BigDecimal("200.00"));
        attempt2.setAttemptStatus("FAILED");
        attempt2.setAttemptDate(LocalDateTime.now());
        attempt2.setErrorMessage("Insufficient funds");
        paymentAttemptRepository.save(attempt2);
    }

    @Test
    public void testFindById() {
        Optional<PaymentAttempt> found = paymentAttemptRepository.findById(attempt1.getAttemptId());

        assertThat(found).isPresent();
        assertThat(found.get().getAttemptStatus()).isEqualTo(attempt1.getAttemptStatus());
    }

    @Test
    public void testFindByUserId() {
        List<PaymentAttempt> attempts = paymentAttemptRepository.findByUserId(userId);

        assertThat(attempts).hasSize(2);
        assertThat(attempts).allMatch(a -> a.getUserId().equals(userId));
    }

    @Test
    public void testFindByAttemptStatus() {
        List<PaymentAttempt> successfulAttempts = paymentAttemptRepository.findByAttemptStatus("SUCCESS");
        List<PaymentAttempt> failedAttempts = paymentAttemptRepository.findByAttemptStatus("FAILED");

        assertThat(successfulAttempts).hasSize(1);
        assertThat(failedAttempts).hasSize(1);
    }

    @Test
    public void testFindByAttemptDateBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<PaymentAttempt> attempts = paymentAttemptRepository.findByAttemptDateBetween(startTime, endTime);

        assertThat(attempts).hasSize(2);
    }

    @Test
    public void testFindByAmountGreaterThan() {
        List<PaymentAttempt> largeAttempts = paymentAttemptRepository.findByAmountGreaterThan(new BigDecimal("150.00"));

        assertThat(largeAttempts).hasSize(1);
        assertThat(largeAttempts.get(0).getAmount()).isGreaterThan(new BigDecimal("150.00"));
    }

    @Test
    public void testFindByPaymentMethodMethodId() {
        List<PaymentAttempt> attempts = paymentAttemptRepository.findByPaymentMethodMethodId(paymentMethod.getMethodId());

        assertThat(attempts).hasSize(2);
    }

    @Test
    public void testFindByErrorMessageIsNotNull() {
        List<PaymentAttempt> attemptsWithError = paymentAttemptRepository.findByErrorMessageIsNotNull();

        assertThat(attemptsWithError).hasSize(1);
    }

    @Test
    public void testFindFirstByUserIdOrderByAttemptDateDesc() {
        Optional<PaymentAttempt> latestAttempt = paymentAttemptRepository.findFirstByUserIdOrderByAttemptDateDesc(userId);

        assertThat(latestAttempt).isPresent();
    }

    @Test
    public void testFindByUserIdAndAttemptStatus() {
        List<PaymentAttempt> userSuccessAttempts = paymentAttemptRepository.findByUserIdAndAttemptStatus(userId, "SUCCESS");

        assertThat(userSuccessAttempts).hasSize(1);
    }

    @Test
    public void testSoftDelete() {
        PaymentAttempt attempt = paymentAttemptRepository.findById(attempt1.getAttemptId()).orElseThrow();
        attempt.setDeletedAt(LocalDateTime.now());
        attempt.setDeletedBy("testUser");
        paymentAttemptRepository.save(attempt);

        Optional<PaymentAttempt> deleted = paymentAttemptRepository.findById(attempt1.getAttemptId());
        assertThat(deleted).isEmpty();
    }

    @Test
    public void testCountByUserIdAndAttemptStatus() {
        long successCount = paymentAttemptRepository.countByUserIdAndAttemptStatus(userId, "SUCCESS");

        assertThat(successCount).isEqualTo(1);
    }

    @Test
    public void testFindByUserIdAndAttemptDateAfter() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        List<PaymentAttempt> recentAttempts = paymentAttemptRepository.findByUserIdAndAttemptDateAfter(userId, startTime);

        assertThat(recentAttempts).hasSize(1);
    }

    @Test
    public void testFindByAttemptStatusIn() {
        List<String> statuses = Arrays.asList("SUCCESS", "FAILED");
        List<PaymentAttempt> attempts = paymentAttemptRepository.findByAttemptStatusIn(statuses);

        assertThat(attempts).hasSize(2);
    }

    @Test
    public void testUpdateAttemptStatus() {
        PaymentAttempt attempt = paymentAttemptRepository.findById(attempt1.getAttemptId()).orElseThrow();
        attempt.setAttemptStatus("PROCESSING");
        PaymentAttempt updated = paymentAttemptRepository.save(attempt);

        assertThat(updated.getAttemptStatus()).isEqualTo("PROCESSING");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
