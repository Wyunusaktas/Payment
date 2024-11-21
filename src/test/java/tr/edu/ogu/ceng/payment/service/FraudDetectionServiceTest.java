package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.FraudDetectionDTO;
import tr.edu.ogu.ceng.payment.entity.FraudDetection;
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

    @MockBean
    private FraudDetectionRepository fraudDetectionRepository;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private ModelMapper modelMapper;

    private FraudDetection fraudDetection;
    private FraudDetectionDTO fraudDetectionDTO;

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

        fraudDetectionDTO = modelMapper.map(fraudDetection, FraudDetectionDTO.class);
    }


    @Test
    void testCreateFraudDetection() {
        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        FraudDetectionDTO createdFraudDetectionDTO = fraudDetectionService.save(fraudDetectionDTO);

        assertNotNull(createdFraudDetectionDTO, "FraudDetection creation failed, returned object is null.");
        assertEquals(fraudDetectionDTO.getFraudCaseId(), createdFraudDetectionDTO.getFraudCaseId());
        verify(fraudDetectionRepository, times(1)).save(any(FraudDetection.class));
    }

    @Test
    void testFindFraudDetectionById_NotFound() {
        when(fraudDetectionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<FraudDetectionDTO> foundFraudDetectionDTO = fraudDetectionService.findById(999L);

        assertFalse(foundFraudDetectionDTO.isPresent(), "FraudDetection should not be found.");
        verify(fraudDetectionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindFraudDetectionById() {
        when(fraudDetectionRepository.findById(fraudDetection.getFraudCaseId())).thenReturn(Optional.of(fraudDetection));

        Optional<FraudDetectionDTO> foundFraudDetectionDTO = fraudDetectionService.findById(fraudDetection.getFraudCaseId());

        assertTrue(foundFraudDetectionDTO.isPresent(), "FraudDetection not found.");
        assertEquals(fraudDetectionDTO.getFraudCaseId(), foundFraudDetectionDTO.get().getFraudCaseId());
        verify(fraudDetectionRepository, times(1)).findById(fraudDetection.getFraudCaseId());
    }

    @Test
    void testUpdateFraudDetection() {
        // Test veri setini güncelleyerek "Resolved" durumunu ayarla
        fraudDetectionDTO.setStatus("Resolved");

        // mock save işlemi sırasında FraudDetection nesnesinin geri dönüşünü güncel durum ile ayarla
        FraudDetection updatedFraudDetection = new FraudDetection();
        updatedFraudDetection.setStatus("Resolved");
        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(updatedFraudDetection);

        // Servis çağrısı ve dönen değerlerin doğrulanması
        FraudDetectionDTO updatedFraudDetectionDTO = fraudDetectionService.save(fraudDetectionDTO);

        assertNotNull(updatedFraudDetectionDTO, "FraudDetection update failed, returned object is null.");
        assertEquals("Resolved", updatedFraudDetectionDTO.getStatus(), "Status did not update correctly.");
        verify(fraudDetectionRepository, times(1)).save(any(FraudDetection.class));
    }


    @Test
    void testSoftDeleteFraudDetection() {
        ArgumentCaptor<FraudDetection> captor = ArgumentCaptor.forClass(FraudDetection.class);

        when(fraudDetectionRepository.findById(fraudDetection.getFraudCaseId())).thenReturn(Optional.of(fraudDetection));
        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        fraudDetectionService.softDelete(fraudDetectionDTO.getFraudCaseId(), "testUser");

        verify(fraudDetectionRepository, times(1)).findById(fraudDetectionDTO.getFraudCaseId());
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

        List<FraudDetectionDTO> fraudDetections = fraudDetectionService.findAll();

        assertNotNull(fraudDetections, "FraudDetection list is null.");
        assertFalse(fraudDetections.isEmpty(), "FraudDetection list is empty.");
        assertEquals(1, fraudDetections.size(), "FraudDetection list size mismatch.");
        verify(fraudDetectionRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsEmptyWhenNotFound() {
        when(fraudDetectionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<FraudDetectionDTO> result = fraudDetectionService.findById(1L);

        assertFalse(result.isPresent(), "Should return Optional.empty() if fraud detection not found");
    }

    @Test
    void testUpdateFraudDetectionWithNegativeScore() {
        fraudDetectionDTO.setFraudScore(BigDecimal.valueOf(-5.0)); // Negatif değer

        when(fraudDetectionRepository.save(any(FraudDetection.class))).thenReturn(fraudDetection);

        FraudDetectionDTO updatedFraudDetectionDTO = fraudDetectionService.save(fraudDetectionDTO);

        assertNotNull(updatedFraudDetectionDTO, "FraudDetection update failed, returned object is null.");
        assertTrue(updatedFraudDetectionDTO.getFraudScore().compareTo(BigDecimal.ZERO) >= 0, "Fraud score should be non-negative");
    }

}
