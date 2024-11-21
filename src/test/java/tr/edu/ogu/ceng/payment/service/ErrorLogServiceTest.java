package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.ErrorLogDTO;
import tr.edu.ogu.ceng.payment.entity.ErrorLog;
import tr.edu.ogu.ceng.payment.repository.ErrorLogRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class ErrorLogServiceTest {

    @MockBean
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private ModelMapper modelMapper;

    private ErrorLog errorLog;
    private ErrorLogDTO errorLogDTO;

    @BeforeEach
    void setUp() {
        Mockito.reset(errorLogRepository);

        errorLog = new ErrorLog();
        errorLog.setErrorId(1L);
        errorLog.setErrorMessage("Test error message");
        errorLog.setStackTrace("Test stack trace");
        errorLog.setOccurredAt(LocalDateTime.now());
        errorLog.setResolved(false);

        errorLogDTO = modelMapper.map(errorLog, ErrorLogDTO.class);
    }


    @Test
    void testCreateErrorLog() {
        when(errorLogRepository.save(any(ErrorLog.class))).thenReturn(errorLog);

        ErrorLogDTO createdErrorLogDTO = errorLogService.save(errorLogDTO);

        assertNotNull(createdErrorLogDTO, "ErrorLog creation failed, returned object is null.");
        assertEquals(errorLogDTO.getErrorId(), createdErrorLogDTO.getErrorId());
        verify(errorLogRepository, times(1)).save(any(ErrorLog.class));
    }

    @Test
    void testFindErrorLogById_NotFound() {
        when(errorLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ErrorLogDTO> foundErrorLogDTO = errorLogService.findById(999L);

        assertFalse(foundErrorLogDTO.isPresent(), "ErrorLog should not be found.");
        verify(errorLogRepository, times(1)).findById(999L);
    }

    @Test
    void testFindErrorLogById() {
        when(errorLogRepository.findById(errorLog.getErrorId())).thenReturn(Optional.of(errorLog));

        Optional<ErrorLogDTO> foundErrorLogDTO = errorLogService.findById(errorLog.getErrorId());

        assertTrue(foundErrorLogDTO.isPresent(), "ErrorLog not found.");
        assertEquals(errorLogDTO.getErrorId(), foundErrorLogDTO.get().getErrorId());
        verify(errorLogRepository, times(1)).findById(errorLog.getErrorId());
    }

    @Test
    void testUpdateErrorLog() {
        errorLogDTO.setResolved(true);

        // Güncellenmiş ErrorLog nesnesini ayarla
        ErrorLog updatedErrorLog = new ErrorLog();
        updatedErrorLog.setResolved(true);  // Güncellenmiş durumu ayarlayın
        when(errorLogRepository.save(any(ErrorLog.class))).thenReturn(updatedErrorLog);

        ErrorLogDTO updatedErrorLogDTO = errorLogService.save(errorLogDTO);

        assertNotNull(updatedErrorLogDTO, "ErrorLog update failed, returned object is null.");
        assertTrue(updatedErrorLogDTO.isResolved(), "ErrorLog resolution status did not update correctly.");
        verify(errorLogRepository, times(1)).save(any(ErrorLog.class));
    }


    @Test
    void testSoftDeleteErrorLog() {
        ArgumentCaptor<ErrorLog> captor = ArgumentCaptor.forClass(ErrorLog.class);

        when(errorLogRepository.findById(errorLog.getErrorId())).thenReturn(Optional.of(errorLog));
        when(errorLogRepository.save(any(ErrorLog.class))).thenReturn(errorLog);

        errorLogService.softDelete(errorLogDTO.getErrorId(), "testUser");

        verify(errorLogRepository, times(1)).findById(errorLogDTO.getErrorId());
        verify(errorLogRepository, times(1)).save(captor.capture());

        ErrorLog softDeletedErrorLog = captor.getValue();
        assertNotNull(softDeletedErrorLog.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedErrorLog.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteErrorLog_NotFound() {
        when(errorLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        errorLogService.softDelete(999L, "testUser");

        verify(errorLogRepository, times(1)).findById(999L);
        verify(errorLogRepository, never()).save(any(ErrorLog.class));
    }

    @Test
    void testFindAllErrorLogs() {
        when(errorLogRepository.findAll()).thenReturn(List.of(errorLog));

        List<ErrorLogDTO> errorLogs = errorLogService.findAll();

        assertNotNull(errorLogs, "ErrorLog list is null.");
        assertFalse(errorLogs.isEmpty(), "ErrorLog list is empty.");
        assertEquals(1, errorLogs.size(), "ErrorLog list size mismatch.");
        verify(errorLogRepository, times(1)).findAll();
    }

    @Test
    void testFindAllErrorLogsEmpty() {
        when(errorLogRepository.findAll()).thenReturn(Collections.emptyList());

        List<ErrorLogDTO> errorLogs = errorLogService.findAll();

        assertTrue(errorLogs.isEmpty(), "findAll should return empty list if no error logs exist");
    }

    @Test
    void testUpdateErrorLogWithEmptyMessage() {
        errorLogDTO.setErrorMessage("");

        when(errorLogRepository.save(any(ErrorLog.class))).thenReturn(errorLog);

        ErrorLogDTO updatedErrorLogDTO = errorLogService.save(errorLogDTO);

        assertNotNull(updatedErrorLogDTO, "ErrorLog update failed, returned object is null.");
        assertNotEquals("", updatedErrorLogDTO.getErrorMessage(), "Error message should not be empty");
        verify(errorLogRepository, times(1)).save(any(ErrorLog.class));
    }

    @Test
    void testUpdateErrorLogResolvedStatus() {
        // errorLog nesnesinin resolved alanını güncel olarak ayarlayın
        errorLog.setResolved(true);
        errorLogDTO.setResolved(true);

        // Repository save işleminde updated errorLog'u döndür
        when(errorLogRepository.save(any(ErrorLog.class))).thenReturn(errorLog);

        ErrorLogDTO updatedErrorLogDTO = errorLogService.save(errorLogDTO);

        assertNotNull(updatedErrorLogDTO, "ErrorLog update failed, returned object is null.");
        assertTrue(updatedErrorLogDTO.isResolved(), "ErrorLog should be marked as resolved");
        verify(errorLogRepository, times(1)).save(any(ErrorLog.class));
    }




}
