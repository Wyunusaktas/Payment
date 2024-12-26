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
import tr.edu.ogu.ceng.payment.entity.Refund;

@SpringBootTest
public class RefundRepositoryTest  extends  TestContainerConfig {

  

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

  

    @BeforeEach
    public void setUp() {
        refundRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void testSaveRefund() {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Set the Payment object instead of paymentId
        refund.setRefundAmount(BigDecimal.valueOf(50.75));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("PENDING");

        Refund savedRefund = refundRepository.save(refund);
        assertThat(savedRefund).isNotNull();
        assertThat(savedRefund.getRefundId()).isNotNull();
    }

    @Test
    public void testFindByPaymentId() {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = paymentRepository.save(payment);

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Set the Payment object
        refund.setRefundAmount(BigDecimal.valueOf(50.00));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("COMPLETED");
        refundRepository.save(refund);

        List<Refund> refunds = refundRepository.findByPayment_PaymentId(savedPayment.getPaymentId());
        assertThat(refunds).hasSize(1);
        assertThat(refunds.get(0).getPayment().getPaymentId()).isEqualTo(savedPayment.getPaymentId());
    }

    @Test
    public void testFindByStatus() {
        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(75.00));
        payment1.setStatus("COMPLETED");
        payment1.setTransactionDate(LocalDateTime.now());
        payment1.setDescription("Payment 1");
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(100.00));
        payment2.setStatus("COMPLETED");
        payment2.setTransactionDate(LocalDateTime.now());
        payment2.setDescription("Payment 2");
        paymentRepository.save(payment2);

        Refund refund1 = new Refund();
        refund1.setRefundId(UUID.randomUUID());
        refund1.setPayment(payment1);  // Set the Payment object
        refund1.setRefundAmount(BigDecimal.valueOf(50.00));
        refund1.setRefundDate(LocalDateTime.now());
        refund1.setStatus("PENDING");
        refundRepository.save(refund1);

        Refund refund2 = new Refund();
        refund2.setRefundId(UUID.randomUUID());
        refund2.setPayment(payment2);  // Set the Payment object
        refund2.setRefundAmount(BigDecimal.valueOf(100.00));
        refund2.setRefundDate(LocalDateTime.now());
        refund2.setStatus("COMPLETED");
        refundRepository.save(refund2);

        List<Refund> pendingRefunds = refundRepository.findByStatus("PENDING");
        assertThat(pendingRefunds).hasSize(1);
        assertThat(pendingRefunds.get(0).getStatus()).isEqualTo("PENDING");

        List<Refund> completedRefunds = refundRepository.findByStatus("COMPLETED");
        assertThat(completedRefunds).hasSize(1);
        assertThat(completedRefunds.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    public void testFindByRefundDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Payment payment1 = new Payment();
        payment1.setUserId(UUID.randomUUID());
        payment1.setAmount(BigDecimal.valueOf(75.00));
        payment1.setStatus("COMPLETED");
        payment1.setTransactionDate(LocalDateTime.now().minusHours(5));
        payment1.setDescription("Payment 1");
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(100.00));
        payment2.setStatus("COMPLETED");
        payment2.setTransactionDate(LocalDateTime.now().plusHours(5));
        payment2.setDescription("Payment 2");
        paymentRepository.save(payment2);

        Refund refund1 = new Refund();
        refund1.setRefundId(UUID.randomUUID());
        refund1.setPayment(payment1);
        refund1.setRefundAmount(BigDecimal.valueOf(50.00));
        refund1.setRefundDate(LocalDateTime.now().minusHours(5));
        refund1.setStatus("COMPLETED");
        refundRepository.save(refund1);

        Refund refund2 = new Refund();
        refund2.setRefundId(UUID.randomUUID());
        refund2.setPayment(payment2);
        refund2.setRefundAmount(BigDecimal.valueOf(100.00));
        refund2.setRefundDate(LocalDateTime.now().plusHours(5));
        refund2.setStatus("PENDING");
        refundRepository.save(refund2);

        List<Refund> refunds = refundRepository.findByRefundDateBetween(startDate, endDate);
        assertThat(refunds).hasSize(2);
    }

    @Test
    public void testCalculateTotalRefundAmount() {
        // Create and save Payment objects first with status set
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.50));
        payment.setStatus("PENDING");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
        paymentRepository.save(payment); // Save the Payment to persist it in DB
    
        Payment payment2 = new Payment();
        payment2.setUserId(UUID.randomUUID());
        payment2.setAmount(BigDecimal.valueOf(100.50));
        payment2.setStatus("PENDING");
        payment2.setTransactionDate(LocalDateTime.now());
        payment2.setDescription("Test Payment");
        payment2.setRecurring(false);
        payment2.setPaymentChannel("Online");
        paymentRepository.save(payment2); // Save the Payment to persist it in DB
    
    
        // Create and save Refund objects
        Refund refund1 = new Refund();
        refund1.setRefundId(UUID.randomUUID());
        refund1.setPayment(payment);  // Associate with a persisted Payment
        refund1.setRefundAmount(BigDecimal.valueOf(75.00));
        refund1.setRefundDate(LocalDateTime.now());
        refund1.setStatus("COMPLETED");
        refundRepository.save(refund1);
    
        Refund refund2 = new Refund();
        refund2.setRefundId(UUID.randomUUID());
        refund2.setPayment(payment2);  // Associate with a persisted Payment
        refund2.setRefundAmount(BigDecimal.valueOf(100.00));
        refund2.setRefundDate(LocalDateTime.now());
        refund2.setStatus("COMPLETED");
        refundRepository.save(refund2);
    
        // Calculate total refund amount
        BigDecimal totalRefundAmount = refundRepository.calculateTotalRefundAmount();
        
        // Assert the expected total refund amount
        assertThat(totalRefundAmount).isEqualByComparingTo(BigDecimal.valueOf(175.00));
    }

    @Test
public void testCalculateTotalRefundAmountByStatus() {
    Payment payment = new Payment();
    payment.setUserId(UUID.randomUUID());
    payment.setAmount(BigDecimal.valueOf(100.50));
    payment.setStatus("PENDING");
    payment.setTransactionDate(LocalDateTime.now());
    payment.setDescription("Test Payment");
    payment.setRecurring(false);
    payment.setPaymentChannel("Online");
    paymentRepository.save(payment);  // Save the Payment

    Payment payment2 = new Payment();
    payment2.setUserId(UUID.randomUUID());
    payment2.setAmount(BigDecimal.valueOf(100.50));
    payment2.setStatus("PENDING");
    payment2.setTransactionDate(LocalDateTime.now());
    payment2.setDescription("Test Payment");
    payment2.setRecurring(false);
    payment2.setPaymentChannel("Online");
    paymentRepository.save(payment2); 

    // Create and save Refund objects
    Refund refund1 = new Refund();
    refund1.setRefundId(UUID.randomUUID());
    refund1.setPayment(payment);  // Associate with the first Payment
    refund1.setRefundAmount(BigDecimal.valueOf(60.00));
    refund1.setRefundDate(LocalDateTime.now());
    refund1.setStatus("COMPLETED");
    refundRepository.save(refund1);

    Refund refund2 = new Refund();
    refund2.setRefundId(UUID.randomUUID());
    refund2.setPayment(payment2);  // Associate with the second Payment
    refund2.setRefundAmount(BigDecimal.valueOf(40.00));
    refund2.setRefundDate(LocalDateTime.now());
    refund2.setStatus("PENDING");
    refundRepository.save(refund2);

    // Calculate total refund amount for "COMPLETED" status
    BigDecimal completedAmount = refundRepository.calculateTotalRefundAmountByStatus("COMPLETED");
    assertThat(completedAmount).isEqualByComparingTo(BigDecimal.valueOf(60.00));

    // Calculate total refund amount for "PENDING" status
    BigDecimal pendingAmount = refundRepository.calculateTotalRefundAmountByStatus("PENDING");
    assertThat(pendingAmount).isEqualByComparingTo(BigDecimal.valueOf(40.00));
}

}
