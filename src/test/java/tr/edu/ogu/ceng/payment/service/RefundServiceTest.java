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
import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;
import tr.edu.ogu.ceng.payment.repository.RefundRepository;

public class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RefundService refundService;

    private UUID paymentId;
    private UUID refundId;
    private Refund refund;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito mocks'larını başlat

        // Test için örnek Payment nesnesi oluşturuyoruz
        payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        // Payment nesnesi save edilmesi bekleniyor
        when(paymentRepository.save(payment)).thenReturn(payment);
        paymentId = payment.getPaymentId();  // paymentId burada alınır

        // Test için örnek Refund nesnesi oluşturuyoruz
        refundId = UUID.randomUUID();
        refund = new Refund();
        refund.setRefundId(refundId);
        refund.setPayment(payment);  // Payment nesnesini set ediyoruz
        refund.setRefundAmount(BigDecimal.valueOf(50.00));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("COMPLETED");

        // Refund nesnesi save edilmesi bekleniyor
        when(refundRepository.save(refund)).thenReturn(refund);
    }

    @Test
    public void testGetRefundsByPaymentId() {
        // Mock: Ödeme ID'sine ait iadeleri döndüren metodu mockla
        when(refundRepository.findByPayment_PaymentId(paymentId)).thenReturn(List.of(refund));

        // Servis metodu çağrılır
        List<Refund> refunds = refundService.getRefundsByPaymentId(paymentId);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(refunds);
        assertEquals(1, refunds.size());
        assertEquals(paymentId, refunds.get(0).getPayment().getPaymentId());  // paymentId'yi kontrol ediyoruz
    }

    @Test
    public void testGetRefundsByStatus() {
        // Mock: İade durumuna göre iadeleri döndüren metodu mockla
        when(refundRepository.findByStatus("COMPLETED")).thenReturn(List.of(refund));

        // Servis metodu çağrılır
        List<Refund> refunds = refundService.getRefundsByStatus("COMPLETED");

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(refunds);
        assertEquals(1, refunds.size());
        assertEquals("COMPLETED", refunds.get(0).getStatus());
    }

    @Test
    public void testGetRefundsByDateRange() {
        // Mock: Tarih aralığındaki iadeleri döndüren metodu mockla
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(refundRepository.findByRefundDateBetween(startDate, endDate)).thenReturn(List.of(refund));

        // Servis metodu çağrılır
        List<Refund> refunds = refundService.getRefundsByDateRange(startDate, endDate);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(refunds);
        assertEquals(1, refunds.size());
        assertTrue(refunds.get(0).getRefundDate().isAfter(startDate) && refunds.get(0).getRefundDate().isBefore(endDate));
    }

    @Test
    public void testCalculateTotalRefundAmount() {
        // Mock: Tüm iadelerin toplam tutarını hesaplayan metodu mockla
        when(refundRepository.calculateTotalRefundAmount()).thenReturn(BigDecimal.valueOf(500.00));

        // Servis metodu çağrılır
        BigDecimal totalAmount = refundService.calculateTotalRefundAmount();

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(totalAmount);
        assertEquals(BigDecimal.valueOf(500.00), totalAmount);
    }

    @Test
    public void testCalculateTotalRefundAmountByStatus() {
        // Mock: Duruma göre iadelerin toplam tutarını hesaplayan metodu mockla
        when(refundRepository.calculateTotalRefundAmountByStatus("COMPLETED")).thenReturn(BigDecimal.valueOf(200.00));

        // Servis metodu çağrılır
        BigDecimal totalAmount = refundService.calculateTotalRefundAmountByStatus("COMPLETED");

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(totalAmount);
        assertEquals(BigDecimal.valueOf(200.00), totalAmount);
    }

    @Test
    public void testAddRefund() {
        // Mock: Yeni bir iade kaydını ekler
        when(refundRepository.save(refund)).thenReturn(refund);

        // Servis metodu çağrılır
        Refund savedRefund = refundService.addRefund(refund);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(savedRefund);
        assertEquals(refund.getRefundId(), savedRefund.getRefundId());
    }

    @Test
    public void testUpdateRefund() {
        // Mock: İade kaydının güncellenmesi
        when(refundRepository.existsById(refund.getRefundId())).thenReturn(true);
        when(refundRepository.save(refund)).thenReturn(refund);

        // Servis metodu çağrılır
        Refund updatedRefund = refundService.updateRefund(refund);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(updatedRefund);
        assertEquals(refund.getRefundId(), updatedRefund.getRefundId());
    }

    @Test
    public void testUpdateRefundNotFound() {
        // Mock: İade kaydının bulunmaması durumunu belirt
        when(refundRepository.existsById(refund.getRefundId())).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refundService.updateRefund(refund);
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("İade bulunamadı.", exception.getMessage());
    }

    @Test
    public void testDeleteRefund() {
        // Mock: İade kaydının mevcut olduğunu belirt
        when(refundRepository.existsById(refund.getRefundId())).thenReturn(true);

        // Servis metodu çağrılır
        refundService.deleteRefund(refund.getRefundId());

        // Veritabanı erişimi kontrolü yapılır
        verify(refundRepository, times(1)).deleteById(refund.getRefundId());
    }

    @Test
    public void testDeleteRefundNotFound() {
        // Mock: İade kaydının mevcut olmadığını belirt
        when(refundRepository.existsById(refund.getRefundId())).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refundService.deleteRefund(refund.getRefundId());
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("İade bulunamadı.", exception.getMessage());
    }
}
