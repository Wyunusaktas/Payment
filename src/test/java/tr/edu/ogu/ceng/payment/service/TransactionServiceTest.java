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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.Transaction;
import tr.edu.ogu.ceng.payment.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID paymentId;
    private UUID transactionId;
    private Transaction transaction;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        // Payment nesnesi oluşturuluyor
        payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        // Payment ID'yi alıyoruz
        paymentId = payment.getPaymentId();

        // Transaction nesnesi oluşturuluyor ve Payment ile ilişkilendiriliyor
        transactionId = UUID.randomUUID();
        transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setPayment(payment); // Payment nesnesini ilişkilendiriyoruz
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(BigDecimal.valueOf(100.00));
    }

    @Test
    public void testGetTransactionsByPaymentId() {
        // Mock: paymentId ile işlemleri döndür
        when(transactionRepository.findByPayment_PaymentId(paymentId)).thenReturn(List.of(transaction));

        // Servis metodu çağrılır
        List<Transaction> result = transactionService.getTransactionsByPaymentId(paymentId);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentId, result.get(0).getPayment().getPaymentId());
    }

    @Test
    public void testGetTransactionsByStatus() {
        // Mock: status ile işlemleri döndür
        when(transactionRepository.findByStatus("COMPLETED")).thenReturn(List.of(transaction));

        // Servis metodu çağrılır
        List<Transaction> result = transactionService.getTransactionsByStatus("COMPLETED");

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).getStatus());
    }

    @Test
    public void testGetTransactionsByDateRange() {
        // Mock: belirli tarih aralığında işlemleri döndür
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(List.of(transaction));

        // Servis metodu çağrılır
        List<Transaction> result = transactionService.getTransactionsByDateRange(startDate, endDate);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTransactionDate().isAfter(startDate));
        assertTrue(result.get(0).getTransactionDate().isBefore(endDate));
    }

    @Test
    public void testCalculateTotalTransactionAmount() {
        // Mock: toplam işlem tutarını döndür
        when(transactionRepository.calculateTotalTransactionAmount()).thenReturn(BigDecimal.valueOf(100.00));

        // Servis metodu çağrılır
        BigDecimal result = transactionService.calculateTotalTransactionAmount();

        // Assert: Sonucun doğru olduğunu kontrol et
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.00), result);
    }

    @Test
    public void testCalculateTotalAmountByPaymentId() {
        // Mock: paymentId'ye göre toplam tutarı döndür
        when(transactionRepository.calculateTotalAmountByPaymentId(paymentId)).thenReturn(BigDecimal.valueOf(100.00));

        // Servis metodu çağrılır
        BigDecimal result = transactionService.calculateTotalAmountByPaymentId(paymentId);

        // Assert: Sonucun doğru olduğunu kontrol et
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.00), result);
    }

    @Test
    public void testAddTransaction() {
        // Mock: yeni işlem kaydını kaydeder
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Servis metodu çağrılır
        Transaction result = transactionService.addTransaction(transaction);

        // Assert: Sonucun doğru olduğunu kontrol et
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
    }

    @Test
    public void testUpdateTransaction() {
        // Mock: işlem var, güncelleme yapılabilir
        when(transactionRepository.existsById(transactionId)).thenReturn(true);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Servis metodu çağrılır
        Transaction result = transactionService.updateTransaction(transaction);

        // Assert: Sonucun doğru olduğunu kontrol et
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
    }

    @Test
    public void testDeleteTransaction() {
        // Mock: işlem var, silinebilir
        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        // Servis metodu çağrılır
        transactionService.deleteTransaction(transactionId);

        // Verify: transactionRepository.deleteById() metodunun çağrıldığını doğrula
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }
}
