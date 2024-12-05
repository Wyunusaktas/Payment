package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
import tr.edu.ogu.ceng.payment.repository.TransactionHistoryRepository;

public class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    private TransactionHistory transactionHistory;
    private UUID userId;
    private UUID paymentId;
    private UUID historyId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito mocks'larını başlat

        // UUID ve Payment nesnelerini oluştur
        userId = UUID.randomUUID();
        paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());  // Ensure this is within the range
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");


        // TransactionHistory nesnesini oluştur
        transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId);
        transactionHistory.setPayment(payment);  // Payment objesini ilişkilendir
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());  // Ensure this is within the range
        transactionHistory.setStatus("COMPLETED");
        historyId = transactionHistory.getHistoryId();  // historyId'yi set et

        // Repository metodu için mocklar
        when(transactionHistoryRepository.findByUserId(userId)).thenReturn(List.of(transactionHistory));
        when(transactionHistoryRepository.findByPayment_PaymentId(paymentId)).thenReturn(List.of(transactionHistory));
        when(transactionHistoryRepository.findByTransactionType("WITHDRAWAL")).thenReturn(List.of(transactionHistory));
        when(transactionHistoryRepository.calculateTotalAmountByUserId(userId)).thenReturn(BigDecimal.valueOf(1000.00));
        when(transactionHistoryRepository.save(transactionHistory)).thenReturn(transactionHistory);
        when(transactionHistoryRepository.existsById(historyId)).thenReturn(true);
    }

    @Test
    public void testGetTransactionHistoryByUserId() {
        // Servis metodu çağrılır
        List<TransactionHistory> result = transactionHistoryService.getTransactionHistoryByUserId(userId);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    public void testGetTransactionHistoryByPaymentId() {
        // String paymentId'yi UUID'ye çevir
        String paymentIdString = "b0f3d28f-4de8-4677-87a2-4f42335ec95d";
        UUID paymentId = UUID.fromString(paymentIdString);  // String'i UUID'ye çevir
    
        // Test verisi için TransactionHistory nesnesi oluştur
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setPayment(new Payment());
        transactionHistory.getPayment().setPaymentId(paymentId);  // UUID'yi set et
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());
        transactionHistory.setStatus("COMPLETED");
    
        // Mock: paymentId ile işlem geçmişini döndüren metodu mockla
        when(transactionHistoryRepository.findByPayment_PaymentId(paymentId)).thenReturn(List.of(transactionHistory));
    
        // Servis metodu çağrılır
        List<TransactionHistory> result = transactionHistoryService.getTransactionHistoryByPaymentId(paymentId);
    
        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);  // Sonucun null olmaması gerektiğini kontrol et
        assertEquals(1, result.size());  // Liste boyutunun 1 olduğunu doğrula
        assertEquals(paymentId, result.get(0).getPayment().getPaymentId());  // paymentId'nin doğru olduğunu kontrol et
    }
    

    @Test
    public void testGetTransactionHistoryByTransactionType() {
        // Servis metodu çağrılır
        List<TransactionHistory> result = transactionHistoryService.getTransactionHistoryByTransactionType("WITHDRAWAL");

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WITHDRAWAL", result.get(0).getTransactionType());
    }

    @Test
    public void testGetTransactionHistoryByDateRange() {
        // Mock: Tarih aralığına göre işlem geçmişini döndüren metodu mockla
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);  // Testin geçerli tarih aralığı
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    
        // TransactionHistory nesnesi oluştur ve tarih aralığını belirle
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUserId(userId);
        transactionHistory.setPayment(new Payment());  // Payment nesnesini ilişkilendir
        transactionHistory.setTransactionType("WITHDRAWAL");
        transactionHistory.setAmount(BigDecimal.valueOf(50.00));
        transactionHistory.setTransactionDate(LocalDateTime.now());  // Bu tarih aralığı içinde olmalı
        transactionHistory.setStatus("COMPLETED");
    
        // Mock: Belirtilen tarih aralığındaki işlem geçmişini döndür
        when(transactionHistoryRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(List.of(transactionHistory));
    
        // Servis metodu çağrılır
        List<TransactionHistory> result = transactionHistoryService.getTransactionHistoryByDateRange(startDate, endDate);
    
        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());  // Beklenen sonucun 1 olması gerektiğini doğrula
        assertTrue(result.get(0).getTransactionDate().isAfter(startDate) && result.get(0).getTransactionDate().isBefore(endDate));
    }
    

    @Test
    public void testCalculateTotalAmountByUserId() {
        // Servis metodu çağrılır
        BigDecimal totalAmount = transactionHistoryService.calculateTotalAmountByUserId(userId);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(totalAmount);
        assertEquals(BigDecimal.valueOf(1000.00), totalAmount);
    }

    @Test
    public void testAddTransactionHistory() {
        // Servis metodu çağrılır
        TransactionHistory result = transactionHistoryService.addTransactionHistory(transactionHistory);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(transactionHistory.getHistoryId(), result.getHistoryId());
    }

    @Test
    public void testUpdateTransactionHistory() {
        // Servis metodu çağrılır
        TransactionHistory result = transactionHistoryService.updateTransactionHistory(transactionHistory);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(transactionHistory.getHistoryId(), result.getHistoryId());
    }

    @Test
    public void testUpdateTransactionHistoryNotFound() {
        // İşlem geçmişi kaydının bulunmadığını belirt
        when(transactionHistoryRepository.existsById(historyId)).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionHistoryService.updateTransactionHistory(transactionHistory);
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("İşlem geçmişi bulunamadı.", exception.getMessage());
    }

    @Test
    public void testDeleteTransactionHistory() {
        // Servis metodu çağrılır
        transactionHistoryService.deleteTransactionHistory(historyId);

        // Veritabanı erişimi kontrolü yapılır
        verify(transactionHistoryRepository, times(1)).deleteById(historyId);
    }

    @Test
    public void testDeleteTransactionHistoryNotFound() {
        // İşlem geçmişi kaydının mevcut olmadığını belirt
        when(transactionHistoryRepository.existsById(historyId)).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionHistoryService.deleteTransactionHistory(historyId);
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("İşlem geçmişi bulunamadı.", exception.getMessage());
    }
}
