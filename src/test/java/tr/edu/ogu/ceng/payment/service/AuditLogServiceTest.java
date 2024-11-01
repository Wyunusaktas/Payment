package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.modelmapper.ModelMapper;
import tr.edu.ogu.ceng.payment.dto.AuditLogDTO;
import tr.edu.ogu.ceng.payment.entity.AuditLog;
import tr.edu.ogu.ceng.payment.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Testcontainers
@ActiveProfiles("test")
public class AuditLogServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ModelMapper modelMapper;

    private AuditLogDTO auditLogDTO;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        Mockito.reset(auditLogRepository);

        auditLog = new AuditLog();
        auditLog.setLogId(1L);
        auditLog.setActionType("CREATE");
        auditLog.setUserId(UUID.randomUUID());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDescription("Audit log created");

        auditLogDTO = modelMapper.map(auditLog, AuditLogDTO.class);
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        AuditLogDTO createdAuditLogDTO = auditLogService.save(auditLogDTO);

        assertNotNull(createdAuditLogDTO, "AuditLogDTO creation failed, returned object is null.");
        assertEquals(auditLogDTO.getLogId(), createdAuditLogDTO.getLogId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testFindAuditLogById_NotFound() {
        when(auditLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<AuditLogDTO> foundAuditLog = auditLogService.findById(999L);

        assertFalse(foundAuditLog.isPresent(), "AuditLogDTO should not be found.");
        verify(auditLogRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAuditLogById() {
        when(auditLogRepository.findById(auditLog.getLogId())).thenReturn(Optional.of(auditLog));

        Optional<AuditLogDTO> foundAuditLogDTO = auditLogService.findById(auditLogDTO.getLogId());

        assertTrue(foundAuditLogDTO.isPresent(), "AuditLogDTO not found.");
        assertEquals(auditLogDTO.getLogId(), foundAuditLogDTO.get().getLogId());
        verify(auditLogRepository, times(1)).findById(auditLog.getLogId());
    }

    @Test
    void testUpdateAuditLog() {
        auditLogDTO.setDescription("Updated description");

        // mock save işlemi sırasında AuditLog nesnesinin geri dönüşünü ayarla
        AuditLog updatedAuditLog = new AuditLog();
        updatedAuditLog.setDescription("Updated description");
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(updatedAuditLog);

        // Servis çağrısı ve dönen değerlerin doğrulanması
        AuditLogDTO updatedAuditLogDTO = auditLogService.save(auditLogDTO);

        assertNotNull(updatedAuditLogDTO, "AuditLogDTO update failed, returned object is null.");
        assertEquals("Updated description", updatedAuditLogDTO.getDescription(), "Description did not update correctly");
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testUpdateAuditLogWithEmptyDescription() {
        auditLogDTO.setDescription(""); // Boş açıklama ayarlandı

        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        AuditLogDTO updatedAuditLogDTO = auditLogService.save(auditLogDTO);

        assertNotNull(updatedAuditLogDTO, "AuditLog update failed, returned object is null.");
        assertNotEquals("", updatedAuditLogDTO.getDescription(), "AuditLog description should not be empty");
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }



    @Test
    void testSoftDeleteAuditLog() {
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        when(auditLogRepository.findById(auditLog.getLogId())).thenReturn(Optional.of(auditLog));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        auditLogService.softDelete(auditLogDTO.getLogId(), "testUser");

        verify(auditLogRepository, times(1)).findById(auditLog.getLogId());
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog softDeletedAuditLog = captor.getValue();
        assertNotNull(softDeletedAuditLog.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedAuditLog.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteAuditLog_NotFound() {
        when(auditLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        auditLogService.softDelete(999L, "testUser");

        verify(auditLogRepository, times(1)).findById(999L);
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void testFindAllAuditLogs() {
        when(auditLogRepository.findAll()).thenReturn(List.of(auditLog));

        List<AuditLogDTO> auditLogDTOs = auditLogService.findAll();

        assertNotNull(auditLogDTOs, "AuditLogDTO list is null.");
        assertFalse(auditLogDTOs.isEmpty(), "AuditLogDTO list is empty.");
        assertEquals(1, auditLogDTOs.size(), "AuditLogDTO list size mismatch.");
        verify(auditLogRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWithInvalidId() {
        Long invalidId = 999L;
        when(auditLogRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<AuditLogDTO> result = auditLogService.findById(invalidId);

        assertFalse(result.isPresent(), "Invalid ID should return Optional.empty()");
    }
}
