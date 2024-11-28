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

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.ThirdPartyPayment;

@SpringBootTest
public class ThirdPartyPaymentRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ThirdPartyPaymentRepository thirdPartyPaymentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private ThirdPartyPayment payment1;
    private ThirdPartyPayment payment2;
    private Payment basePayment;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        // Ana ödeme kaydı oluştur
        basePayment = new Payment();
        basePayment.setUserId(UUID.randomUUID());
        basePayment.setAmount(new BigDecimal("100.00"));
        basePayment.setStatus("COMPLETED");
        paymentRepository.save(basePayment);

        // PayPal ödemesi
        payment1 = new ThirdPartyPayment();
        payment1.setPayment(basePayment);
        payment1.setProvider("PayPal");
        payment1.setTransactionReference("PP-123456789");
        payment1.setStatus("COMPLETED");
        payment1.setProcessedAt(LocalDateTime.now().minusHours(2));
        thirdPartyPaymentRepository.save(payment1);

        // Stripe ödemesi
        payment2 = new ThirdPartyPayment();
        payment2.setPayment(basePayment);
        payment2.setProvider("Stripe");
        payment2.setTransactionReference("STR-987654321");
        payment2.setStatus("PENDING");
        payment2.setProcessedAt(LocalDateTime.now());
        thirdPartyPaymentRepository.save(payment2);
    }

    @Test
    public void testFindById() {
        Optional<ThirdPartyPayment> found = thirdPartyPaymentRepository.findById(payment1.getThirdPartyPaymentId());

        assertThat(found).isPresent();
        assertThat(found.get().getProvider()).isEqualTo(payment1.getProvider());
    }

    @Test
    public void testFindByProvider() {
        List<ThirdPartyPayment> paypalPayments = thirdPartyPaymentRepository.findByProvider("PayPal");
        List<ThirdPartyPayment> stripePayments = thirdPartyPaymentRepository.findByProvider("Stripe");

        assertThat(paypalPayments).hasSize(1);
        assertThat(stripePayments).hasSize(1);
    }

    @Test
    public void testFindByTransactionReference() {
        Optional<ThirdPartyPayment> payment = thirdPartyPaymentRepository.findByTransactionReference("PP-123456789");

        assertThat(payment).isPresent();
        assertThat(payment.get().getProvider()).isEqualTo("PayPal");
    }

    @Test
    public void testFindByStatus() {
        List<ThirdPartyPayment> completedPayments = thirdPartyPaymentRepository.findByStatus("COMPLETED");
        List<ThirdPartyPayment> pendingPayments = thirdPartyPaymentRepository.findByStatus("PENDING");

        assertThat(completedPayments).hasSize(1);
        assertThat(pendingPayments).hasSize(1);
    }

    @Test
    public void testFindByProcessedAtBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<ThirdPartyPayment> payments = thirdPartyPaymentRepository.findByProcessedAtBetween(startTime, endTime);

        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<ThirdPartyPayment> payments = thirdPartyPaymentRepository.findByPaymentPaymentId(basePayment.getPaymentId());

        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByProviderAndStatus() {
        List<ThirdPartyPayment> completedPaypalPayments = thirdPartyPaymentRepository
                .findByProviderAndStatus("PayPal", "COMPLETED");

        assertThat(completedPaypalPayments).hasSize(1);
        assertThat(completedPaypalPayments.get(0).getProvider()).isEqualTo("PayPal");
    }

    @Test
    public void testFindFirstByOrderByProcessedAtDesc() {
        Optional<ThirdPartyPayment> latestPayment = thirdPartyPaymentRepository.findFirstByOrderByProcessedAtDesc();

        assertThat(latestPayment).isPresent();
        assertThat(latestPayment.get().getTransactionReference()).isEqualTo(payment2.getTransactionReference());
    }

    @Test
    public void testSoftDelete() {
        ThirdPartyPayment payment = thirdPartyPaymentRepository.findById(payment1.getThirdPartyPaymentId()).orElseThrow();
        payment.setDeletedAt(LocalDateTime.now());
        payment.setDeletedBy("testUser");
        thirdPartyPaymentRepository.save(payment);

        Optional<ThirdPartyPayment> deletedPayment = thirdPartyPaymentRepository.findById(payment1.getThirdPartyPaymentId());
        assertThat(deletedPayment).isEmpty();
    }

    @Test
    public void testUpdatePaymentStatus() {
        ThirdPartyPayment payment = thirdPartyPaymentRepository.findById(payment2.getThirdPartyPaymentId()).orElseThrow();
        payment.setStatus("COMPLETED");
        thirdPartyPaymentRepository.save(payment);

        ThirdPartyPayment updatedPayment = thirdPartyPaymentRepository.findById(payment2.getThirdPartyPaymentId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo("COMPLETED");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
