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

import tr.edu.ogu.ceng.payment.entity.FraudDetection;
import tr.edu.ogu.ceng.payment.entity.Payment;

@SpringBootTest
public class FraudDetectionRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private FraudDetectionRepository fraudDetectionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private FraudDetection fraudCase1;
    private FraudDetection fraudCase2;
    private Payment payment;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        fraudCase1 = new FraudDetection();
        fraudCase1.setPayment(payment);
        fraudCase1.setUserId(userId);
        fraudCase1.setSuspiciousReason("Unusual amount");
        fraudCase1.setFraudScore(new BigDecimal("75.5"));
        fraudCase1.setStatus("PENDING");
        fraudCase1.setReportedAt(LocalDateTime.now().minusDays(1));
        fraudDetectionRepository.save(fraudCase1);

        fraudCase2 = new FraudDetection();
        fraudCase2.setPayment(payment);
        fraudCase2.setUserId(userId);
        fraudCase2.setSuspiciousReason("Multiple attempts");
        fraudCase2.setFraudScore(new BigDecimal("90.0"));
        fraudCase2.setStatus("CONFIRMED");
        fraudCase2.setReportedAt(LocalDateTime.now());
        fraudCase2.setResolvedAt(LocalDateTime.now());
        fraudDetectionRepository.save(fraudCase2);
    }

    @Test
    public void testFindById() {
        Optional<FraudDetection> found = fraudDetectionRepository.findById(fraudCase1.getFraudCaseId());

        assertThat(found).isPresent();
        assertThat(found.get().getSuspiciousReason()).isEqualTo(fraudCase1.getSuspiciousReason());
    }

    @Test
    public void testFindByUserId() {
        List<FraudDetection> foundCases = fraudDetectionRepository.findByUserId(userId);

        assertThat(foundCases).hasSize(2);
        assertThat(foundCases).allMatch(fc -> fc.getUserId().equals(userId));
    }

    @Test
    public void testFindByStatus() {
        List<FraudDetection> pendingCases = fraudDetectionRepository.findByStatus("PENDING");
        List<FraudDetection> confirmedCases = fraudDetectionRepository.findByStatus("CONFIRMED");

        assertThat(pendingCases).hasSize(1);
        assertThat(confirmedCases).hasSize(1);
    }

    @Test
    public void testFindByFraudScoreGreaterThan() {
        List<FraudDetection> highRiskCases = fraudDetectionRepository.findByFraudScoreGreaterThan(new BigDecimal("80.0"));

        assertThat(highRiskCases).hasSize(1);
        assertThat(highRiskCases.get(0).getFraudScore()).isGreaterThan(new BigDecimal("80.0"));
    }

    @Test
    public void testFindByReportedAtBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<FraudDetection> cases = fraudDetectionRepository.findByReportedAtBetween(startDate, endDate);

        assertThat(cases).hasSize(2);
    }

    @Test
    public void testFindUnresolvedCases() {
        List<FraudDetection> unresolvedCases = fraudDetectionRepository.findByResolvedAtIsNull();

        assertThat(unresolvedCases).hasSize(1);
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<FraudDetection> cases = fraudDetectionRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(cases).hasSize(2);
        assertThat(cases).allMatch(fc -> fc.getPayment().equals(payment));
    }

    @Test
    public void testSoftDelete() {
        FraudDetection fraudCase = fraudDetectionRepository.findById(fraudCase1.getFraudCaseId()).orElseThrow();
        fraudCase.setDeletedAt(LocalDateTime.now());
        fraudCase.setDeletedBy("testUser");
        fraudDetectionRepository.save(fraudCase);

        Optional<FraudDetection> deletedCase = fraudDetectionRepository.findById(fraudCase1.getFraudCaseId());
        assertThat(deletedCase).isEmpty();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
