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

import tr.edu.ogu.ceng.payment.common.TestContainerConfig;
import tr.edu.ogu.ceng.payment.entity.Payment;

@SpringBootTest
public class PaymentRepositoryTest  extends  TestContainerConfig {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

   

    @BeforeEach
    public void setUp() {
        paymentRepository.deleteAll();
        paymentMethodRepository.deleteAll();
    }

    @Test
    public void testSavePayment() {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.50));
        payment.setStatus("PENDING");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getPaymentId()).isNotNull();
    }

    @Test
    public void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(50.75));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());

        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByUserId(userId);
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.75));
    }

    @Test
    public void testFindByStatus() {
        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(25.00));
        payment1.setStatus("PENDING");
        payment1.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(50.00));
        payment2.setStatus("COMPLETED");
        payment2.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment2);

        List<Payment> pendingPayments = paymentRepository.findByStatus("PENDING");
        assertThat(pendingPayments).hasSize(1);
        assertThat(pendingPayments.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(25.00));

        List<Payment> completedPayments = paymentRepository.findByStatus("COMPLETED");
        assertThat(completedPayments).hasSize(1);
        assertThat(completedPayments.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }


    @Test
    public void testFindByTransactionDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(30.00));
        payment1.setStatus("COMPLETED");
        payment1.setTransactionDate(LocalDateTime.now().minusHours(5));
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(40.00));
        payment2.setStatus("PENDING");
        payment2.setTransactionDate(LocalDateTime.now().plusHours(5));
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findByTransactionDateBetween(startDate, endDate);
        assertThat(payments).hasSize(2);
    }

    @Test
    public void testFindByUserIdAndDateRange() {
        UUID userId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Payment payment1 = new Payment();
        payment1.setUserId(userId);
        payment1.setAmount(BigDecimal.valueOf(25.00));
        payment1.setStatus("PENDING");
        payment1.setTransactionDate(LocalDateTime.now().minusHours(5));
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(userId);
        payment2.setAmount(BigDecimal.valueOf(35.00));
        payment2.setStatus("COMPLETED");
        payment2.setTransactionDate(LocalDateTime.now().plusHours(5));
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        assertThat(payments).hasSize(2);
    }

    @Test
    public void testCalculateTotalAmount() {
        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(100.00));
        payment1.setStatus("COMPLETED");
        payment1.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(50.00));
        payment2.setStatus("COMPLETED");
        payment2.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment2);

        BigDecimal totalAmount = paymentRepository.calculateTotalAmount();
        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    public void testCalculateTotalAmountByStatus() {
        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(50.00));
        payment1.setStatus("COMPLETED");
        payment1.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(25.00));
        payment2.setStatus("PENDING");
        payment2.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment2);

        BigDecimal completedAmount = paymentRepository.calculateTotalAmountByStatus("COMPLETED");
        assertThat(completedAmount).isEqualByComparingTo(BigDecimal.valueOf(50.00));

        BigDecimal pendingAmount = paymentRepository.calculateTotalAmountByStatus("PENDING");
        assertThat(pendingAmount).isEqualByComparingTo(BigDecimal.valueOf(25.00));
    }
}
