package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID userId;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();  // Yeni bir userId oluşturuluyor

        payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(100.50));
        payment.setStatus("Completed");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
    }

    @Test
    public void testGetAllPaymentsByUserId() {
        // Repository davranışını taklit et
        when(paymentRepository.findByUserId(userId)).thenReturn(List.of(payment));

        List<Payment> payments = paymentService.getAllPaymentsByUserId(userId);

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals(payment.getUserId(), payments.get(0).getUserId());

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetPaymentsByStatus() {
        // Repository davranışını taklit et
        when(paymentRepository.findByStatus("Completed")).thenReturn(List.of(payment));

        List<Payment> payments = paymentService.getPaymentsByStatus("Completed");

        assertNotNull(payments);
        assertTrue(payments.size() > 0);
        assertEquals("Completed", payments.get(0).getStatus());

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).findByStatus("Completed");
    }

    @Test
    public void testGetPaymentsByPaymentMethod() {
        UUID methodId = UUID.randomUUID(); // Taklit edilen methodId
        when(paymentRepository.findByPaymentMethod_MethodId(methodId)).thenReturn(List.of(payment));

        List<Payment> payments = paymentService.getPaymentsByPaymentMethod(methodId);

        assertNotNull(payments);
        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).findByPaymentMethod_MethodId(methodId);
    }

    @Test
    public void testGetPaymentsByDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(paymentRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(List.of(payment));

        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);

        assertNotNull(payments);
        assertTrue(payments.size() > 0);
        assertTrue(payments.get(0).getTransactionDate().isAfter(startDate));
        assertTrue(payments.get(0).getTransactionDate().isBefore(endDate));

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).findByTransactionDateBetween(startDate, endDate);
    }

    @Test
    public void testCalculateTotalAmount() {
        when(paymentRepository.calculateTotalAmount()).thenReturn(BigDecimal.valueOf(500.00));

        BigDecimal totalAmount = paymentService.calculateTotalAmount();

        assertNotNull(totalAmount);
        assertEquals(0, totalAmount.compareTo(BigDecimal.valueOf(500.00)));

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).calculateTotalAmount();
    }

    @Test
    public void testCalculateTotalAmountByStatus() {
        when(paymentRepository.calculateTotalAmountByStatus("Completed")).thenReturn(BigDecimal.valueOf(100.00));

        BigDecimal totalAmount = paymentService.calculateTotalAmountByStatus("Completed");

        assertNotNull(totalAmount);
        assertEquals(0, totalAmount.compareTo(BigDecimal.valueOf(100.00)));

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).calculateTotalAmountByStatus("Completed");
    }

    @Test
    public void testAddPayment() {
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment savedPayment = paymentService.addPayment(payment);

        assertNotNull(savedPayment);
        assertEquals(payment.getAmount(), savedPayment.getAmount());

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    public void testUpdatePayment() {
        payment.setStatus("Refunded");

        when(paymentRepository.existsById(payment.getPaymentId())).thenReturn(true);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment updatedPayment = paymentService.updatePayment(payment);

        assertNotNull(updatedPayment);
        assertEquals("Refunded", updatedPayment.getStatus());

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).existsById(payment.getPaymentId());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    public void testDeletePayment() {
        UUID paymentId = payment.getPaymentId();

        when(paymentRepository.existsById(paymentId)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(paymentId);

        paymentService.deletePayment(paymentId);

        // Repository metodunun çağrıldığını doğrula
        verify(paymentRepository, times(1)).existsById(paymentId);
        verify(paymentRepository, times(1)).deleteById(paymentId);
    }
}
