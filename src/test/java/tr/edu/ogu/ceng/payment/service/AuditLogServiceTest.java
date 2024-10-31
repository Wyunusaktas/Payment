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
import tr.edu.ogu.ceng.payment.model.AuditLog;
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
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditLogServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

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

        AuditLog createdAuditLog = auditLogService.save(auditLog);

        assertNotNull(createdAuditLog, "AuditLog creation failed, returned object is null.");
        assertEquals(auditLog.getLogId(), createdAuditLog.getLogId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testFindAuditLogById_NotFound() {
        when(auditLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<AuditLog> foundAuditLog = auditLogService.findById(999L);

        assertFalse(foundAuditLog.isPresent(), "AuditLog should not be found.");
        verify(auditLogRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAuditLogById() {
        when(auditLogRepository.findById(auditLog.getLogId())).thenReturn(Optional.of(auditLog));

        Optional<AuditLog> foundAuditLog = auditLogService.findById(auditLog.getLogId());

        assertTrue(foundAuditLog.isPresent(), "AuditLog not found.");
        assertEquals(auditLog.getLogId(), foundAuditLog.get().getLogId());
        verify(auditLogRepository, times(1)).findById(auditLog.getLogId());
    }

    @Test
    void testUpdateAuditLog() {
        auditLog.setDescription("Updated description");

        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        AuditLog updatedAuditLog = auditLogService.save(auditLog);

        assertNotNull(updatedAuditLog, "AuditLog update failed, returned object is null.");
        assertEquals("Updated description", updatedAuditLog.getDescription());
        verify(auditLogRepository, times(1)).save(auditLog);
    }

    @Test
    void testSoftDeleteAuditLog() {
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        when(auditLogRepository.findById(auditLog.getLogId())).thenReturn(Optional.of(auditLog));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        auditLogService.softDelete(auditLog.getLogId(), "testUser");

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

        List<AuditLog> auditLogs = auditLogService.findAll();

        assertNotNull(auditLogs, "AuditLog list is null.");
        assertFalse(auditLogs.isEmpty(), "AuditLog list is empty.");
        assertEquals(1, auditLogs.size(), "AuditLog list size mismatch.");
        verify(auditLogRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWithInvalidId() {
        Long invalidId = 999L;
        when(auditLogRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<AuditLog> result = auditLogService.findById(invalidId);

        assertFalse(result.isPresent(), "Invalid ID should return Optional.empty()");
    }


    // Diğer testler...
}
