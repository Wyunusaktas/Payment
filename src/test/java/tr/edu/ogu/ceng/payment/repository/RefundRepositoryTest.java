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
import tr.edu.ogu.ceng.payment.entity.Refund;

@SpringBootTest
public class RefundRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Refund refund1;
    private Refund refund2;
    private Payment payment;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);

        refund1 = new Refund();
        refund1.setPayment(payment);
        refund1.setRefundAmount(new BigDecimal("200.00"));
        refund1.setRefundReason("Defective product");
        refund1.setStatus("PENDING");
        refund1.setRefundDate(LocalDateTime.now().minusDays(1));
        refund1.setRefundMethod("CREDIT_CARD");
        refundRepository.save(refund1);

        refund2 = new Refund();
        refund2.setPayment(payment);
        refund2.setRefundAmount(new BigDecimal("300.00"));
        refund2.setRefundReason("Wrong size");
        refund2.setStatus("COMPLETED");
        refund2.setRefundDate(LocalDateTime.now());
        refund2.setRefundMethod("BANK_TRANSFER");
        refund2.setRefundIssuedAt(LocalDateTime.now());
        refundRepository.save(refund2);
    }

    @Test
    public void testFindById() {
        Optional<Refund> found = refundRepository.findById(refund1.getRefundId());

        assertThat(found).isPresent();
        assertThat(found.get().getRefundReason()).isEqualTo(refund1.getRefundReason());
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<Refund> refunds = refundRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(refunds).hasSize(2);
        assertThat(refunds).allMatch(r -> r.getPayment().equals(payment));
    }

    @Test
    public void testFindByStatus() {
        List<Refund> pendingRefunds = refundRepository.findByStatus("PENDING");
        List<Refund> completedRefunds = refundRepository.findByStatus("COMPLETED");

        assertThat(pendingRefunds).hasSize(1);
        assertThat(completedRefunds).hasSize(1);
    }

    @Test
    public void testFindByRefundDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Refund> refunds = refundRepository.findByRefundDateBetween(startDate, endDate);

        assertThat(refunds).hasSize(2);
    }

    @Test
    public void testFindByRefundMethod() {
        List<Refund> creditCardRefunds = refundRepository.findByRefundMethod("CREDIT_CARD");
        List<Refund> bankTransferRefunds = refundRepository.findByRefundMethod("BANK_TRANSFER");

        assertThat(creditCardRefunds).hasSize(1);
        assertThat(bankTransferRefunds).hasSize(1);
    }

    @Test
    public void testFindByRefundAmountGreaterThan() {
        List<Refund> largeRefunds = refundRepository.findByRefundAmountGreaterThan(new BigDecimal("250.00"));

        assertThat(largeRefunds).hasSize(1);
    }

    @Test
    public void testFindByRefundIssuedAtIsNotNull() {
        List<Refund> issuedRefunds = refundRepository.findByRefundIssuedAtIsNotNull();

        assertThat(issuedRefunds).hasSize(1);
        assertThat(issuedRefunds.get(0).getRefundIssuedAt()).isNotNull();
    }

    @Test
    public void testCalculateTotalRefundsByPayment() {
        BigDecimal totalRefunds = refundRepository.findByPaymentPaymentId(payment.getPaymentId())
                .stream()
                .map(Refund::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalRefunds).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    public void testSoftDelete() {
        Refund refund = refundRepository.findById(refund1.getRefundId()).orElseThrow();
        refund.setDeletedAt(LocalDateTime.now());
        refund.setDeletedBy("testUser");
        refundRepository.save(refund);

        Optional<Refund> deletedRefund = refundRepository.findById(refund1.getRefundId());
        assertThat(deletedRefund).isEmpty();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
