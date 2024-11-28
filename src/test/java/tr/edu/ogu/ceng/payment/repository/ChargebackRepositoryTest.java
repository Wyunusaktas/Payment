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

import tr.edu.ogu.ceng.payment.entity.Chargeback;
import tr.edu.ogu.ceng.payment.entity.Payment;

@SpringBootTest
public class ChargebackRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ChargebackRepository chargebackRepository;

    private Chargeback chargeback1;
    private Chargeback chargeback2;
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
        payment.setStatus("COMPLETED");
        chargebackRepository.save(payment);

        chargeback1 = new Chargeback();
        chargeback1.setPayment(payment);
        chargeback1.setUserId(userId);
        chargeback1.setChargebackAmount(new BigDecimal("500.00"));
        chargeback1.setReason("Unauthorized transaction");
        chargeback1.setStatus("PENDING");
        chargeback1.setFiledAt(LocalDateTime.now().minusDays(1));
        chargebackRepository.save(chargeback1);

        chargeback2 = new Chargeback();
        chargeback2.setPayment(payment);
        chargeback2.setUserId(userId);
        chargeback2.setChargebackAmount(new BigDecimal("300.00"));
        chargeback2.setReason("Product not received");
        chargeback2.setStatus("RESOLVED");
        chargeback2.setFiledAt(LocalDateTime.now());
        chargeback2.setResolvedAt(LocalDateTime.now());
        chargebackRepository.save(chargeback2);
    }

    @Test
    public void testFindById() {
        Optional<Chargeback> found = chargebackRepository.findById(chargeback1.getChargebackId());

        assertThat(found).isPresent();
        assertThat(found.get().getReason()).isEqualTo(chargeback1.getReason());
    }

    @Test
    public void testFindByUserId() {
        List<Chargeback> chargebacks = chargebackRepository.findByUserId(userId);

        assertThat(chargebacks).hasSize(2);
        assertThat(chargebacks).allMatch(cb -> cb.getUserId().equals(userId));
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<Chargeback> chargebacks = chargebackRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(chargebacks).hasSize(2);
        assertThat(chargebacks).allMatch(cb -> cb.getPayment().equals(payment));
    }

    @Test
    public void testFindByStatus() {
        List<Chargeback> pendingChargebacks = chargebackRepository.findByStatus("PENDING");
        List<Chargeback> resolvedChargebacks = chargebackRepository.findByStatus("RESOLVED");

        assertThat(pendingChargebacks).hasSize(1);
        assertThat(resolvedChargebacks).hasSize(1);
    }

    @Test
    public void testFindByFiledAtBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Chargeback> chargebacks = chargebackRepository.findByFiledAtBetween(startDate, endDate);

        assertThat(chargebacks).hasSize(2);
    }

    @Test
    public void testFindByChargebackAmountGreaterThan() {
        List<Chargeback> largeChargebacks = chargebackRepository.findByChargebackAmountGreaterThan(
                new BigDecimal("400.00")
        );

        assertThat(largeChargebacks).hasSize(1);
        assertThat(largeChargebacks.get(0).getChargebackAmount()).isGreaterThan(new BigDecimal("400.00"));
    }

    @Test
    public void testFindByResolvedAtIsNull() {
        List<Chargeback> unresolvedChargebacks = chargebackRepository.findByResolvedAtIsNull();

        assertThat(unresolvedChargebacks).hasSize(1);
        assertThat(unresolvedChargebacks.get(0).getResolvedAt()).isNull();
    }

    @Test
    public void testFindByResolvedAtIsNotNull() {
        List<Chargeback> resolvedChargebacks = chargebackRepository.findByResolvedAtIsNotNull();

        assertThat(resolvedChargebacks).hasSize(1);
        assertThat(resolvedChargebacks.get(0).getResolvedAt()).isNotNull();
    }

    @Test
    public void testCalculateTotalChargebackAmount() {
        BigDecimal totalChargebacks = chargebackRepository.findAll().stream()
                .map(Chargeback::getChargebackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalChargebacks).isEqualTo(new BigDecimal("800.00"));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
