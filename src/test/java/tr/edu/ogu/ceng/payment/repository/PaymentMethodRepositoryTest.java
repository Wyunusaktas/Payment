package tr.edu.ogu.ceng.payment.repository;

import java.time.LocalDate;
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

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@SpringBootTest
public class PaymentMethodRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        paymentMethodRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void testSavePaymentMethod() {
        // Create a PaymentMethod
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(UUID.randomUUID());
        paymentMethod.setUserId(UUID.randomUUID());
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("1234567890");
        paymentMethod.setDefault(true);
        paymentMethod.setExpiryDate(LocalDate.of(2025, 12, 31));  // Set expiryDate to a non-null value
        
        // Save the PaymentMethod
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        // Assertions
        assertThat(savedPaymentMethod).isNotNull();
        assertThat(savedPaymentMethod.getMethodId()).isNotNull();
        assertThat(savedPaymentMethod.getExpiryDate()).isEqualTo(LocalDate.of(2025, 12, 31));  // Verify the expiryDate
    }
    
    
    @Test
    public void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        
        // Create and save two payment methods for the same user
        PaymentMethod method1 = new PaymentMethod();
        method1.setUserId(userId);
        method1.setType("Credit");
        method1.setProvider("Visa");
        method1.setAccountNumber("1234567890");
        method1.setExpiryDate(LocalDate.of(2025, 12, 31));
        method1.setDefault(true);
        
        PaymentMethod method2 = new PaymentMethod();
        method2.setUserId(userId);
        method2.setType("Digital");
        method2.setProvider("PayPal");
        method2.setAccountNumber("1234567890");
        method2.setExpiryDate(LocalDate.of(2025, 12, 31));
        method2.setDefault(false);
        
        paymentMethodRepository.save(method1);
        paymentMethodRepository.save(method2);
    
        // Find payment methods by userId
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);
    
        // Assertions
        assertThat(paymentMethods).hasSize(2);
        assertThat(paymentMethods.get(0).getUserId()).isEqualTo(userId);
    }
    
    @Test
    public void testFindByUserIdAndIsDefaultTrue() {
        UUID userId = UUID.randomUUID();
        
        // Default ödeme yöntemini oluştur ve kaydet
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUserId(userId);
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("1234567890");
        paymentMethod.setExpiryDate(LocalDate.of(2025, 12, 31));
        paymentMethod.setDefault(true);
        
        paymentMethodRepository.save(paymentMethod);

        // Kullanıcıya ait default ödeme yöntemini bul
        PaymentMethod foundPaymentMethod = paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId);

        // Assert işlemleri
        assertThat(foundPaymentMethod).isNotNull();
        assertThat(foundPaymentMethod.isDefault()).isTrue();
    }
    
    @Test
    public void testFindByType() {
        // userId'yi her iki metod için de doğru şekilde ayarlayın
        UUID userId = UUID.randomUUID();

        // Create and save payment methods
        PaymentMethod creditCardMethod = new PaymentMethod();
        creditCardMethod.setUserId(userId);
        creditCardMethod.setType("Credit");
        creditCardMethod.setProvider("Visa");
        creditCardMethod.setAccountNumber("1234567890");
        creditCardMethod.setExpiryDate(LocalDate.of(2025, 12, 31));
        creditCardMethod.setDefault(true);
        
        PaymentMethod paypalMethod = new PaymentMethod();
        paypalMethod.setUserId(userId);
        paypalMethod.setType("Digital");
        paypalMethod.setProvider("PayPal");
        paypalMethod.setAccountNumber("1234567890");
        paypalMethod.setExpiryDate(LocalDate.of(2025, 12, 31));
        paypalMethod.setDefault(false);
        
        paymentMethodRepository.save(creditCardMethod);
        paymentMethodRepository.save(paypalMethod);
        
        // Find payment methods by type
        List<PaymentMethod> creditMethods = paymentMethodRepository.findByType("Credit");
        
        // Assertions
        assertThat(creditMethods).hasSize(1);
        assertThat(creditMethods.get(0).getType()).isEqualTo("Credit");
    }
    
    @Test
    public void testFindByProvider() {
        // Create and save payment methods
        PaymentMethod creditCardMethod = new PaymentMethod();
        creditCardMethod.setUserId(UUID.randomUUID());
        creditCardMethod.setType("Credit");
        creditCardMethod.setProvider("Visa");
        creditCardMethod.setAccountNumber("1234567890");
        creditCardMethod.setExpiryDate(LocalDate.of(2025, 12, 31));
        creditCardMethod.setDefault(true);
    
        PaymentMethod paypalMethod = new PaymentMethod();
        paypalMethod.setUserId(UUID.randomUUID());
        paypalMethod.setType("Digital");
        paypalMethod.setProvider("PayPal");
        paypalMethod.setAccountNumber("1234567890");
        paypalMethod.setExpiryDate(LocalDate.of(2025, 12, 31));
        paypalMethod.setDefault(false);
    
        paymentMethodRepository.save(creditCardMethod);
        paymentMethodRepository.save(paypalMethod);
    
        // Find payment methods by provider
        List<PaymentMethod> visaMethods = paymentMethodRepository.findByProvider("Visa");
    
        // Assertions
        assertThat(visaMethods).hasSize(1);
        assertThat(visaMethods.get(0).getProvider()).isEqualTo("Visa");
    }
}
