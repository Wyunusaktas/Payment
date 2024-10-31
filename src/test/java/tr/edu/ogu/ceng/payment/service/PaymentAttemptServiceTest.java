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
import tr.edu.ogu.ceng.payment.model.PaymentAttempt;
import tr.edu.ogu.ceng.payment.repository.PaymentAttemptRepository;

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
public class PaymentAttemptServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private PaymentAttemptRepository paymentAttemptRepository;

    @Autowired
    private PaymentAttemptService paymentAttemptService;

    private PaymentAttempt paymentAttempt;

    @BeforeEach
    void setUp() {
        reset(paymentAttemptRepository);

        paymentAttempt = new PaymentAttempt();
        paymentAttempt.setAttemptId(1L);
        paymentAttempt.setUserId(UUID.randomUUID());
        paymentAttempt.setAmount(new BigDecimal("150.00"));
        paymentAttempt.setAttemptStatus("SUCCESS");
        paymentAttempt.setAttemptDate(LocalDateTime.now());
        paymentAttempt.setErrorMessage(null);
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePaymentAttempt() {
        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        PaymentAttempt createdAttempt = paymentAttemptService.save(paymentAttempt);

        assertNotNull(createdAttempt, "PaymentAttempt creation failed, returned object is null.");
        assertEquals(paymentAttempt.getAttemptId(), createdAttempt.getAttemptId());
        verify(paymentAttemptRepository, times(1)).save(any(PaymentAttempt.class));
    }

    @Test
    void testFindPaymentAttemptById_NotFound() {
        when(paymentAttemptRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentAttempt> foundAttempt = paymentAttemptService.findById(999L);

        assertFalse(foundAttempt.isPresent(), "PaymentAttempt should not be found.");
        verify(paymentAttemptRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentAttemptById() {
        when(paymentAttemptRepository.findById(paymentAttempt.getAttemptId())).thenReturn(Optional.of(paymentAttempt));

        Optional<PaymentAttempt> foundAttempt = paymentAttemptService.findById(paymentAttempt.getAttemptId());

        assertTrue(foundAttempt.isPresent(), "PaymentAttempt not found.");
        assertEquals(paymentAttempt.getAttemptId(), foundAttempt.get().getAttemptId());
        verify(paymentAttemptRepository, times(1)).findById(paymentAttempt.getAttemptId());
    }

    @Test
    void testUpdatePaymentAttempt() {
        paymentAttempt.setAttemptStatus("FAILED");

        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        PaymentAttempt updatedAttempt = paymentAttemptService.save(paymentAttempt);

        assertNotNull(updatedAttempt, "PaymentAttempt update failed, returned object is null.");
        assertEquals("FAILED", updatedAttempt.getAttemptStatus(), "Attempt status did not update correctly.");
        verify(paymentAttemptRepository, times(1)).save(paymentAttempt);
    }

    @Test
    void testSoftDeletePaymentAttempt() {
        ArgumentCaptor<PaymentAttempt> captor = ArgumentCaptor.forClass(PaymentAttempt.class);

        when(paymentAttemptRepository.findById(paymentAttempt.getAttemptId())).thenReturn(Optional.of(paymentAttempt));
        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        paymentAttemptService.softDelete(paymentAttempt.getAttemptId(), "testUser");

        verify(paymentAttemptRepository, times(1)).findById(paymentAttempt.getAttemptId());
        verify(paymentAttemptRepository, times(1)).save(captor.capture());

        PaymentAttempt softDeletedAttempt = captor.getValue();
        assertNotNull(softDeletedAttempt.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedAttempt.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentAttempt_NotFound() {
        when(paymentAttemptRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentAttemptService.softDelete(999L, "testUser");

        verify(paymentAttemptRepository, times(1)).findById(999L);
        verify(paymentAttemptRepository, never()).save(any(PaymentAttempt.class));
    }

    @Test
    void testFindAllPaymentAttempts() {
        when(paymentAttemptRepository.findAll()).thenReturn(List.of(paymentAttempt));

        List<PaymentAttempt> attemptsList = paymentAttemptService.findAll();

        assertNotNull(attemptsList, "PaymentAttempt list is null.");
        assertFalse(attemptsList.isEmpty(), "PaymentAttempt list is empty.");
        assertEquals(1, attemptsList.size(), "PaymentAttempt list size mismatch.");
        verify(paymentAttemptRepository, times(1)).findAll();
    }

    @Test
    void testSoftDeleteUpdatesDeletedAtInPaymentAttempt() {
        Long attemptId = 1L;
        PaymentAttempt attempt = new PaymentAttempt();
        when(paymentAttemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        paymentAttemptService.softDelete(attemptId, "testUser");

        assertNotNull(attempt.getDeletedAt(), "Deleted attempt should have a non-null deletedAt field");
        assertEquals("testUser", attempt.getDeletedBy(), "DeletedBy should match the provided user");
    }

}
