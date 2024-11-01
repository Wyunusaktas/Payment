package tr.edu.ogu.ceng.payment.service;

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
import tr.edu.ogu.ceng.payment.dto.SessionDTO;
import tr.edu.ogu.ceng.payment.entity.Session;
import tr.edu.ogu.ceng.payment.repository.SessionRepository;

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
public class SessionServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModelMapper modelMapper;

    private SessionDTO sessionDTO;

    @BeforeEach
    void setUp() {
        reset(sessionRepository);

        sessionDTO = new SessionDTO();
        sessionDTO.setSessionId(1L);
        sessionDTO.setUserId(UUID.randomUUID());
        sessionDTO.setIpAddress("192.168.1.1");
        sessionDTO.setDevice("Laptop");
        sessionDTO.setLocation("Office");
        sessionDTO.setLoginTime(LocalDateTime.now());
        sessionDTO.setStatus("Active");
    }

    @Test
    void testCreateSession() {
        Session session = modelMapper.map(sessionDTO, Session.class);
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        SessionDTO createdSessionDTO = sessionService.save(sessionDTO);

        assertNotNull(createdSessionDTO, "Session creation failed, returned object is null.");
        assertEquals(sessionDTO.getSessionId(), createdSessionDTO.getSessionId());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testFindSessionById_NotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<SessionDTO> foundSessionDTO = sessionService.findById(999L);

        assertFalse(foundSessionDTO.isPresent(), "Session should not be found.");
        verify(sessionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindSessionById() {
        Session session = modelMapper.map(sessionDTO, Session.class);
        when(sessionRepository.findById(sessionDTO.getSessionId())).thenReturn(Optional.of(session));

        Optional<SessionDTO> foundSessionDTO = sessionService.findById(sessionDTO.getSessionId());

        assertTrue(foundSessionDTO.isPresent(), "Session not found.");
        assertEquals(sessionDTO.getSessionId(), foundSessionDTO.get().getSessionId());
        verify(sessionRepository, times(1)).findById(sessionDTO.getSessionId());
    }

    @Test
    void testUpdateSession() {
        sessionDTO.setStatus("Inactive");
        Session updatedSession = modelMapper.map(sessionDTO, Session.class);

        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);

        SessionDTO updatedSessionDTO = sessionService.save(sessionDTO);

        assertNotNull(updatedSessionDTO, "Session update failed, returned object is null.");
        assertEquals("Inactive", updatedSessionDTO.getStatus(), "Status did not update correctly.");
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testSoftDeleteSession() {
        Session session = modelMapper.map(sessionDTO, Session.class);
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);

        when(sessionRepository.findById(sessionDTO.getSessionId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.softDelete(sessionDTO.getSessionId(), "testUser");

        verify(sessionRepository, times(1)).findById(sessionDTO.getSessionId());
        verify(sessionRepository, times(1)).save(captor.capture());

        Session softDeletedSession = captor.getValue();
        assertNotNull(softDeletedSession.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedSession.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteSession_NotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        sessionService.softDelete(999L, "testUser");

        verify(sessionRepository, times(1)).findById(999L);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testFindAllSessions() {
        Session session = modelMapper.map(sessionDTO, Session.class);
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<SessionDTO> sessionDTOList = sessionService.findAll();

        assertNotNull(sessionDTOList, "Session list is null.");
        assertFalse(sessionDTOList.isEmpty(), "Session list is empty.");
        assertEquals(1, sessionDTOList.size(), "Session list size mismatch.");
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testSoftDeleteUpdatesDeletedAtInSession() {
        Long sessionId = 1L;
        Session session = new Session();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        sessionService.softDelete(sessionId, "testUser");

        assertNotNull(session.getDeletedAt(), "Deleted session should have a non-null deletedAt field");
        assertEquals("testUser", session.getDeletedBy(), "DeletedBy should match the provided user");
    }



}
