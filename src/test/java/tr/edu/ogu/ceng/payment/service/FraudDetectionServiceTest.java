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
import tr.edu.ogu.ceng.payment.model.FraudDetection;
import tr.edu.ogu.ceng.payment.repository.FraudDetectionRepository;

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
public class FraudDetectionServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private FraudDetectionRepository fraudDetectionRepository;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    private FraudDetection fraudDetection;

    @BeforeEach
    void setUp() {
        reset(fraudDetectionRepository);

        fraudDetection = new FraudDetection();
        fraudDetection.setFraudCaseId(1L);
        fraudDetection.setUserId(UUID.randomUUID());
        fraudDetection.setFraudScore(new BigDecimal("75.5"));
        fraudDetection.setStatus("Pending");
        fraudDetection.setSuspiciousReason("High transaction frequency");
        fraudDetection.setReportedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateFraudDetection() {
        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        FraudDetection createdFraudDetection = fraudDetectionService.save(fraudDetection);

        assertNotNull(createdFraudDetection, "FraudDetection creation failed, returned object is null.");
        assertEquals(fraudDetection.getFraudCaseId(), createdFraudDetection.getFraudCaseId());
        verify(fraudDetectionRepository, times(1)).save(any(FraudDetection.class));
    }

    @Test
    void testFindFraudDetectionById_NotFound() {
        when(fraudDetectionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<FraudDetection> foundFraudDetection = fraudDetectionService.findById(999L);

        assertFalse(foundFraudDetection.isPresent(), "FraudDetection should not be found.");
        verify(fraudDetectionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindFraudDetectionById() {
        when(fraudDetectionRepository.findById(fraudDetection.getFraudCaseId())).thenReturn(Optional.of(fraudDetection));

        Optional<FraudDetection> foundFraudDetection = fraudDetectionService.findById(fraudDetection.getFraudCaseId());

        assertTrue(foundFraudDetection.isPresent(), "FraudDetection not found.");
        assertEquals(fraudDetection.getFraudCaseId(), foundFraudDetection.get().getFraudCaseId());
        verify(fraudDetectionRepository, times(1)).findById(fraudDetection.getFraudCaseId());
    }

    @Test
    void testUpdateFraudDetection() {
        fraudDetection.setStatus("Resolved");

        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        FraudDetection updatedFraudDetection = fraudDetectionService.save(fraudDetection);

        assertNotNull(updatedFraudDetection, "FraudDetection update failed, returned object is null.");
        assertEquals("Resolved", updatedFraudDetection.getStatus(), "Status did not update correctly.");
        verify(fraudDetectionRepository, times(1)).save(fraudDetection);
    }

    @Test
    void testSoftDeleteFraudDetection() {
        ArgumentCaptor<FraudDetection> captor = ArgumentCaptor.forClass(FraudDetection.class);

        when(fraudDetectionRepository.findById(fraudDetection.getFraudCaseId())).thenReturn(Optional.of(fraudDetection));
        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        fraudDetectionService.softDelete(fraudDetection.getFraudCaseId(), "testUser");

        verify(fraudDetectionRepository, times(1)).findById(fraudDetection.getFraudCaseId());
        verify(fraudDetectionRepository, times(1)).save(captor.capture());

        FraudDetection softDeletedFraudDetection = captor.getValue();
        assertNotNull(softDeletedFraudDetection.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedFraudDetection.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteFraudDetection_NotFound() {
        when(fraudDetectionRepository.findById(anyLong())).thenReturn(Optional.empty());

        fraudDetectionService.softDelete(999L, "testUser");

        verify(fraudDetectionRepository, times(1)).findById(999L);
        verify(fraudDetectionRepository, never()).save(any(FraudDetection.class));
    }

    @Test
    void testFindAllFraudDetections() {
        when(fraudDetectionRepository.findAll()).thenReturn(List.of(fraudDetection));

        List<FraudDetection> fraudDetections = fraudDetectionService.findAll();

        assertNotNull(fraudDetections, "FraudDetection list is null.");
        assertFalse(fraudDetections.isEmpty(), "FraudDetection list is empty.");
        assertEquals(1, fraudDetections.size(), "FraudDetection list size mismatch.");
        verify(fraudDetectionRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsEmptyWhenNotFound() {
        when(fraudDetectionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<FraudDetection> result = fraudDetectionService.findById(1L);

        assertFalse(result.isPresent(), "Should return Optional.empty() if fraud detection not found");
    }

}

