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
import tr.edu.ogu.ceng.payment.model.FailedPayment;
import tr.edu.ogu.ceng.payment.repository.FailedPaymentRepository;

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
public class FailedPaymentServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private FailedPaymentRepository failedPaymentRepository;

    @Autowired
    private FailedPaymentService failedPaymentService;

    private FailedPayment failedPayment;

    @BeforeEach
    void setUp() {
        reset(failedPaymentRepository);

        failedPayment = new FailedPayment();
        failedPayment.setFailedPaymentId(1L);
        failedPayment.setUserId(UUID.randomUUID());
        failedPayment.setAmount(new BigDecimal("150.75"));
        failedPayment.setFailureReason("Insufficient funds");
        failedPayment.setAttemptDate(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateFailedPayment() {
        when(failedPaymentRepository.save(any(FailedPayment.class))).thenReturn(failedPayment);

        FailedPayment createdFailedPayment = failedPaymentService.save(failedPayment);

        assertNotNull(createdFailedPayment, "FailedPayment creation failed, returned object is null.");
        assertEquals(failedPayment.getFailedPaymentId(), createdFailedPayment.getFailedPaymentId());
        verify(failedPaymentRepository, times(1)).save(any(FailedPayment.class));
    }

    @Test
    void testFindFailedPaymentById_NotFound() {
        when(failedPaymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<FailedPayment> foundFailedPayment = failedPaymentService.findById(999L);

        assertFalse(foundFailedPayment.isPresent(), "FailedPayment should not be found.");
        verify(failedPaymentRepository, times(1)).findById(999L);
    }

    @Test
    void testFindFailedPaymentById() {
        when(failedPaymentRepository.findById(failedPayment.getFailedPaymentId())).thenReturn(Optional.of(failedPayment));

        Optional<FailedPayment> foundFailedPayment = failedPaymentService.findById(failedPayment.getFailedPaymentId());

        assertTrue(foundFailedPayment.isPresent(), "FailedPayment not found.");
        assertEquals(failedPayment.getFailedPaymentId(), foundFailedPayment.get().getFailedPaymentId());
        verify(failedPaymentRepository, times(1)).findById(failedPayment.getFailedPaymentId());
    }

    @Test
    void testUpdateFailedPayment() {
        failedPayment.setFailureReason("Account closed");

        when(failedPaymentRepository.save(any(FailedPayment.class))).thenReturn(failedPayment);

        FailedPayment updatedFailedPayment = failedPaymentService.save(failedPayment);

        assertNotNull(updatedFailedPayment, "FailedPayment update failed, returned object is null.");
        assertEquals("Account closed", updatedFailedPayment.getFailureReason(), "Failure reason did not update correctly.");
        verify(failedPaymentRepository, times(1)).save(failedPayment);
    }

    @Test
    void testSoftDeleteFailedPayment() {
        ArgumentCaptor<FailedPayment> captor = ArgumentCaptor.forClass(FailedPayment.class);

        when(failedPaymentRepository.findById(failedPayment.getFailedPaymentId())).thenReturn(Optional.of(failedPayment));
        when(failedPaymentRepository.save(any(FailedPayment.class))).thenReturn(failedPayment);

        failedPaymentService.softDelete(failedPayment.getFailedPaymentId(), "testUser");

        verify(failedPaymentRepository, times(1)).findById(failedPayment.getFailedPaymentId());
        verify(failedPaymentRepository, times(1)).save(captor.capture());

        FailedPayment softDeletedFailedPayment = captor.getValue();
        assertNotNull(softDeletedFailedPayment.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedFailedPayment.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteFailedPayment_NotFound() {
        when(failedPaymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        failedPaymentService.softDelete(999L, "testUser");

        verify(failedPaymentRepository, times(1)).findById(999L);
        verify(failedPaymentRepository, never()).save(any(FailedPayment.class));
    }

    @Test
    void testFindAllFailedPayments() {
        when(failedPaymentRepository.findAll()).thenReturn(List.of(failedPayment));

        List<FailedPayment> failedPayments = failedPaymentService.findAll();

        assertNotNull(failedPayments, "FailedPayment list is null.");
        assertFalse(failedPayments.isEmpty(), "FailedPayment list is empty.");
        assertEquals(1, failedPayments.size(), "FailedPayment list size mismatch.");
        verify(failedPaymentRepository, times(1)).findAll();
    }

    @Test
    void testSaveFailedPaymentSuccess() {
        FailedPayment failedPayment = new FailedPayment();
        failedPayment.setAmount(BigDecimal.valueOf(50.0));
        when(failedPaymentRepository.save(any(FailedPayment.class))).thenReturn(failedPayment);

        FailedPayment result = failedPaymentService.save(failedPayment);

        assertEquals(BigDecimal.valueOf(50.0), result.getAmount(), "Amount should be saved correctly");
    }

}
