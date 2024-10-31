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
import tr.edu.ogu.ceng.payment.model.PaymentMethod;
import tr.edu.ogu.ceng.payment.repository.PaymentMethodRepository;

import java.time.LocalDate;
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
public class PaymentMethodServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodService paymentMethodService;

    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        reset(paymentMethodRepository);

        paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(1L);
        paymentMethod.setUserId(UUID.randomUUID());
        paymentMethod.setType("Credit Card");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("123456789");
        paymentMethod.setExpiryDate(LocalDate.now().plusYears(2));
        paymentMethod.setCreatedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePaymentMethod() {
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethod createdMethod = paymentMethodService.save(paymentMethod);

        assertNotNull(createdMethod, "PaymentMethod creation failed, returned object is null.");
        assertEquals(paymentMethod.getMethodId(), createdMethod.getMethodId());
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void testFindPaymentMethodById_NotFound() {
        when(paymentMethodRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentMethod> foundMethod = paymentMethodService.findById(999L);

        assertFalse(foundMethod.isPresent(), "PaymentMethod should not be found.");
        verify(paymentMethodRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentMethodById() {
        when(paymentMethodRepository.findById(paymentMethod.getMethodId())).thenReturn(Optional.of(paymentMethod));

        Optional<PaymentMethod> foundMethod = paymentMethodService.findById(paymentMethod.getMethodId());

        assertTrue(foundMethod.isPresent(), "PaymentMethod not found.");
        assertEquals(paymentMethod.getMethodId(), foundMethod.get().getMethodId());
        verify(paymentMethodRepository, times(1)).findById(paymentMethod.getMethodId());
    }

    @Test
    void testUpdatePaymentMethod() {
        paymentMethod.setProvider("MasterCard");

        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethod updatedMethod = paymentMethodService.save(paymentMethod);

        assertNotNull(updatedMethod, "PaymentMethod update failed, returned object is null.");
        assertEquals("MasterCard", updatedMethod.getProvider(), "Provider did not update correctly.");
        verify(paymentMethodRepository, times(1)).save(paymentMethod);
    }

    @Test
    void testSoftDeletePaymentMethod() {
        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);

        when(paymentMethodRepository.findById(paymentMethod.getMethodId())).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        paymentMethodService.softDelete(paymentMethod.getMethodId(), "testUser");

        verify(paymentMethodRepository, times(1)).findById(paymentMethod.getMethodId());
        verify(paymentMethodRepository, times(1)).save(captor.capture());

        PaymentMethod softDeletedMethod = captor.getValue();
        assertNotNull(softDeletedMethod.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedMethod.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentMethod_NotFound() {
        when(paymentMethodRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentMethodService.softDelete(999L, "testUser");

        verify(paymentMethodRepository, times(1)).findById(999L);
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testFindAllPaymentMethods() {
        when(paymentMethodRepository.findAll()).thenReturn(List.of(paymentMethod));

        List<PaymentMethod> methodsList = paymentMethodService.findAll();

        assertNotNull(methodsList, "PaymentMethod list is null.");
        assertFalse(methodsList.isEmpty(), "PaymentMethod list is empty.");
        assertEquals(1, methodsList.size(), "PaymentMethod list size mismatch.");
        verify(paymentMethodRepository, times(1)).findAll();
    }

    @Test
    void testSavePaymentMethodNotNull() {
        PaymentMethod method = new PaymentMethod();
        method.setProvider("Visa");
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(method);

        PaymentMethod result = paymentMethodService.save(method);

        assertNotNull(result, "Saved payment method should not be null");
        assertEquals("Visa", result.getProvider(), "Provider should match");
    }

}
