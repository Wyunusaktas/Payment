package tr.edu.ogu.ceng.payment.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.repository.PaymentMethodRepository;

public class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private UUID userId;
    private PaymentMethod paymentMethod;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito mocks'larını başlat
        userId = UUID.randomUUID();
        paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(UUID.randomUUID());
        paymentMethod.setUserId(userId);
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("1234567890");
        paymentMethod.setDefault(true);
    }

    @Test
    public void testGetAllPaymentMethodsByUserId() {
        // Mock: Kullanıcıya ait ödeme yöntemlerini döndür
        when(paymentMethodRepository.findByUserId(userId)).thenReturn(List.of(paymentMethod));

        // Servis metodu çağrılır
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethodsByUserId(userId);

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertNotNull(paymentMethods);
        assertEquals(1, paymentMethods.size());
        assertEquals(paymentMethod.getUserId(), paymentMethods.get(0).getUserId());
    }

    @Test
    public void testGetDefaultPaymentMethod() {
        // Mock: Kullanıcının varsayılan ödeme yöntemini döndür
        when(paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)).thenReturn(paymentMethod);

        // Servis metodu çağrılır
        PaymentMethod result = paymentMethodService.getDefaultPaymentMethod(userId);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertTrue(result.isDefault());
    }

    @Test
    public void testAddPaymentMethod() {
        // Mock: Yeni ödeme yöntemini kaydet
        when(paymentMethodRepository.save(paymentMethod)).thenReturn(paymentMethod);

        // Servis metodu çağrılır
        PaymentMethod savedPaymentMethod = paymentMethodService.addPaymentMethod(paymentMethod);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(savedPaymentMethod);
        assertEquals(paymentMethod.getType(), savedPaymentMethod.getType());
    }

    @Test
    public void testUpdatePaymentMethod() {
        // Mock: Ödeme yönteminin güncellenmesi
        when(paymentMethodRepository.existsById(paymentMethod.getMethodId())).thenReturn(true);
        when(paymentMethodRepository.save(paymentMethod)).thenReturn(paymentMethod);

        // Servis metodu çağrılır
        PaymentMethod updatedPaymentMethod = paymentMethodService.updatePaymentMethod(paymentMethod);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(updatedPaymentMethod);
        assertEquals(paymentMethod.getMethodId(), updatedPaymentMethod.getMethodId());
    }

    @Test
    public void testDeletePaymentMethod() {
        // Mock: Ödeme yönteminin mevcut olduğunu belirt
        when(paymentMethodRepository.existsById(paymentMethod.getMethodId())).thenReturn(true);

        // Servis metodu çağrılır
        paymentMethodService.deletePaymentMethod(paymentMethod.getMethodId());

        // Veritabanı erişimi kontrolü yapılır
        verify(paymentMethodRepository, times(1)).deleteById(paymentMethod.getMethodId());
    }

    @Test
    public void testDeletePaymentMethodNotFound() {
        // Mock: Ödeme yönteminin mevcut olmadığını belirt
        when(paymentMethodRepository.existsById(paymentMethod.getMethodId())).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentMethodService.deletePaymentMethod(paymentMethod.getMethodId());
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("Ödeme yöntemi bulunamadı.", exception.getMessage());
    }

    @Test
    public void testExistsByUserId() {
        // Mock: Kullanıcıya ait ödeme yöntemlerinin var olup olmadığını kontrol et
        when(paymentMethodRepository.findByUserId(userId)).thenReturn(List.of(paymentMethod));

        // Servis metodu çağrılır
        boolean exists = paymentMethodService.existsByUserId(userId);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertTrue(exists);
    }

    @Test
    public void testExistsByUserIdNoMethods() {
        // Mock: Kullanıcıya ait ödeme yöntemlerinin olmadığını belirt
        when(paymentMethodRepository.findByUserId(userId)).thenReturn(List.of());

        // Servis metodu çağrılır
        boolean exists = paymentMethodService.existsByUserId(userId);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertFalse(exists);
    }

    @Test
    public void testGetPaymentMethodById() {
        // Mock: Belirli bir ödeme yöntemini getir
        when(paymentMethodRepository.findById(paymentMethod.getMethodId())).thenReturn(Optional.of(paymentMethod));

        // Servis metodu çağrılır
        Optional<PaymentMethod> result = paymentMethodService.getPaymentMethodById(paymentMethod.getMethodId());

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertTrue(result.isPresent());
        assertEquals(paymentMethod.getMethodId(), result.get().getMethodId());
    }

    @Test
    public void testGetPaymentMethodByIdNotFound() {
        // Mock: Belirli bir ödeme yöntemi bulunamadığında
        when(paymentMethodRepository.findById(paymentMethod.getMethodId())).thenReturn(Optional.empty());

        // Servis metodu çağrılır
        Optional<PaymentMethod> result = paymentMethodService.getPaymentMethodById(paymentMethod.getMethodId());

        // Assert: Sonucun boş olup olmadığını kontrol et
        assertFalse(result.isPresent());
    }
}
