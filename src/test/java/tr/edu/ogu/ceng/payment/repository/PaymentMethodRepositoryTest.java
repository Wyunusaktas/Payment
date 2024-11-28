package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@SpringBootTest
public class PaymentMethodRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private PaymentMethod paymentMethod1;
    private PaymentMethod paymentMethod2;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        paymentMethod1 = new PaymentMethod();
        paymentMethod1.setUserId(userId);
        paymentMethod1.setType("CREDIT_CARD");
        paymentMethod1.setProvider("VISA");
        paymentMethod1.setAccountNumber("4111111111111111");
        paymentMethod1.setExpiryDate(LocalDate.now().plusYears(2));
        paymentMethod1.setIsDefault(true);
        paymentMethod1.setCreatedAt(LocalDateTime.now().minusDays(1));
        paymentMethodRepository.save(paymentMethod1);

        paymentMethod2 = new PaymentMethod();
        paymentMethod2.setUserId(userId);
        paymentMethod2.setType("DEBIT_CARD");
        paymentMethod2.setProvider("MASTERCARD");
        paymentMethod2.setAccountNumber("5555555555554444");
        paymentMethod2.setExpiryDate(LocalDate.now().plusYears(3));
        paymentMethod2.setIsDefault(false);
        paymentMethod2.setCreatedAt(LocalDateTime.now());
        paymentMethodRepository.save(paymentMethod2);
    }

    @Test
    public void testFindById() {
        Optional<PaymentMethod> found = paymentMethodRepository.findById(paymentMethod1.getMethodId());

        assertThat(found).isPresent();
        assertThat(found.get().getAccountNumber()).isEqualTo(paymentMethod1.getAccountNumber());
    }

    @Test
    public void testFindByUserId() {
        List<PaymentMethod> methods = paymentMethodRepository.findByUserId(userId);

        assertThat(methods).hasSize(2);
        assertThat(methods).allMatch(pm -> pm.getUserId().equals(userId));
    }

    @Test
    public void testFindByType() {
        List<PaymentMethod> creditCards = paymentMethodRepository.findByType("CREDIT_CARD");
        List<PaymentMethod> debitCards = paymentMethodRepository.findByType("DEBIT_CARD");

        assertThat(creditCards).hasSize(1);
        assertThat(debitCards).hasSize(1);
    }

    @Test
    public void testFindByProvider() {
        List<PaymentMethod> visaCards = paymentMethodRepository.findByProvider("VISA");
        List<PaymentMethod> mastercardCards = paymentMethodRepository.findByProvider("MASTERCARD");

        assertThat(visaCards).hasSize(1);
        assertThat(mastercardCards).hasSize(1);
    }

    @Test
    public void testFindByIsDefaultTrue() {
        List<PaymentMethod> defaultMethods = paymentMethodRepository.findByIsDefaultTrue();

        assertThat(defaultMethods).hasSize(1);
        assertThat(defaultMethods.get(0).getIsDefault()).isTrue();
    }

    @Test
    public void testFindByExpiryDateBefore() {
        LocalDate futureDate = LocalDate.now().plusYears(5);
        List<PaymentMethod> expiringMethods = paymentMethodRepository.findByExpiryDateBefore(futureDate);

        assertThat(expiringMethods).hasSize(2);
    }

    @Test
    public void testFindByUserIdAndType() {
        List<PaymentMethod> userCreditCards = paymentMethodRepository.findByUserIdAndType(userId, "CREDIT_CARD");

        assertThat(userCreditCards).hasSize(1);
        assertThat(userCreditCards.get(0).getUserId()).isEqualTo(userId);
        assertThat(userCreditCards.get(0).getType()).isEqualTo("CREDIT_CARD");
    }

    @Test
    public void testFindDefaultMethodByUserId() {
        Optional<PaymentMethod> defaultMethod = paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId);

        assertThat(defaultMethod).isPresent();
        assertThat(defaultMethod.get().getIsDefault()).isTrue();
        assertThat(defaultMethod.get().getUserId()).isEqualTo(userId);
    }

    @Test
    public void testSoftDelete() {
        PaymentMethod method = paymentMethodRepository.findById(paymentMethod1.getMethodId()).orElseThrow();
        method.setDeletedAt(LocalDateTime.now());
        method.setDeletedBy("testUser");
        paymentMethodRepository.save(method);

        Optional<PaymentMethod> deletedMethod = paymentMethodRepository.findById(paymentMethod1.getMethodId());
        assertThat(deletedMethod).isEmpty();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
