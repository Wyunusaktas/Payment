package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.model.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class PaymentServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        reset(paymentRepository);

        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("150.75"));
        payment.setStatus("Pending");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test payment description");
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment createdPayment = paymentService.save(payment);

        assertNotNull(createdPayment, "Payment creation failed, returned object is null.");
        assertEquals(payment.getPaymentId(), createdPayment.getPaymentId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testFindPaymentById_NotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Payment> foundPayment = paymentService.findById(999L);

        assertFalse(foundPayment.isPresent(), "Payment should not be found.");
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentById() {
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));

        Optional<Payment> foundPayment = paymentService.findById(payment.getPaymentId());

        assertTrue(foundPayment.isPresent(), "Payment not found.");
        assertEquals(payment.getPaymentId(), foundPayment.get().getPaymentId());
        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
    }

    @Test
    void testUpdatePayment() {
        payment.setStatus("Completed");

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment updatedPayment = paymentService.save(payment);

        assertNotNull(updatedPayment, "Payment update failed, returned object is null.");
        assertEquals("Completed", updatedPayment.getStatus(), "Status did not update correctly.");
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testSoftDeletePayment() {
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.softDelete(payment.getPaymentId(), "testUser");

        verify(paymentRepository, times(1)).findById(payment.getPaymentId());
        verify(paymentRepository, times(1)).save(captor.capture());

        Payment softDeletedPayment = captor.getValue();
        assertNotNull(softDeletedPayment.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedPayment.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePayment_NotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentService.softDelete(999L, "testUser");

        verify(paymentRepository, times(1)).findById(999L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testFindAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> payments = paymentService.findAll();

        assertNotNull(payments, "Payment list is null.");
        assertFalse(payments.isEmpty(), "Payment list is empty.");
        assertEquals(1, payments.size(), "Payment list size mismatch.");
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsPayment() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(200.0));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a payment when the ID is valid");
        assertEquals(BigDecimal.valueOf(200.0), result.get().getAmount(), "Payment amount should match");
    }

}
