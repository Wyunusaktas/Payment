package tr.edu.ogu.ceng.payment.service;

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
import tr.edu.ogu.ceng.payment.model.Session;
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

    private Session session;

    @BeforeEach
    void setUp() {
        reset(sessionRepository);

        session = new Session();
        session.setSessionId(1L);
        session.setUserId(UUID.randomUUID());
        session.setIpAddress("192.168.1.1");
        session.setDevice("Laptop");
        session.setLocation("Office");
        session.setLoginTime(LocalDateTime.now());
        session.setStatus("Active");
    }

    @Test
    void testCreateSession() {
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session createdSession = sessionService.save(session);

        assertNotNull(createdSession, "Session creation failed, returned object is null.");
        assertEquals(session.getSessionId(), createdSession.getSessionId());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testFindSessionById_NotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Session> foundSession = sessionService.findById(999L);

        assertFalse(foundSession.isPresent(), "Session should not be found.");
        verify(sessionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindSessionById() {
        when(sessionRepository.findById(session.getSessionId())).thenReturn(Optional.of(session));

        Optional<Session> foundSession = sessionService.findById(session.getSessionId());

        assertTrue(foundSession.isPresent(), "Session not found.");
        assertEquals(session.getSessionId(), foundSession.get().getSessionId());
        verify(sessionRepository, times(1)).findById(session.getSessionId());
    }

    @Test
    void testUpdateSession() {
        session.setStatus("Inactive");

        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session updatedSession = sessionService.save(session);

        assertNotNull(updatedSession, "Session update failed, returned object is null.");
        assertEquals("Inactive", updatedSession.getStatus(), "Status did not update correctly.");
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testSoftDeleteSession() {
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);

        when(sessionRepository.findById(session.getSessionId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.softDelete(session.getSessionId(), "testUser");

        verify(sessionRepository, times(1)).findById(session.getSessionId());
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
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<Session> sessionList = sessionService.findAll();

        assertNotNull(sessionList, "Session list is null.");
        assertFalse(sessionList.isEmpty(), "Session list is empty.");
        assertEquals(1, sessionList.size(), "Session list size mismatch.");
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
