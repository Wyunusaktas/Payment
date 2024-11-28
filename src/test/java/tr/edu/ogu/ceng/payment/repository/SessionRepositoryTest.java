package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.Session;

@SpringBootTest
public class SessionRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SessionRepository sessionRepository;

    private Session session1;
    private Session session2;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        session1 = new Session();
        session1.setUserId(userId);
        session1.setIpAddress("192.168.1.100");
        session1.setDevice("Chrome/Windows");
        session1.setLocation("Istanbul, Turkey");
        session1.setLoginTime(LocalDateTime.now().minusHours(2));
        session1.setLogoutTime(LocalDateTime.now().minusHours(1));
        session1.setStatus("COMPLETED");
        sessionRepository.save(session1);

        session2 = new Session();
        session2.setUserId(userId);
        session2.setIpAddress("192.168.1.100");
        session2.setDevice("Mobile/iOS");
        session2.setLocation("Istanbul, Turkey");
        session2.setLoginTime(LocalDateTime.now());
        session2.setStatus("ACTIVE");
        sessionRepository.save(session2);
    }

    @Test
    public void testFindById() {
        Optional<Session> found = sessionRepository.findById(session1.getSessionId());

        assertThat(found).isPresent();
        assertThat(found.get().getIpAddress()).isEqualTo(session1.getIpAddress());
    }

    @Test
    public void testFindByUserId() {
        List<Session> sessions = sessionRepository.findByUserId(userId);

        assertThat(sessions).hasSize(2);
        assertThat(sessions).allMatch(s -> s.getUserId().equals(userId));
    }

    @Test
    public void testFindByStatus() {
        List<Session> activeSessions = sessionRepository.findByStatus("ACTIVE");
        List<Session> completedSessions = sessionRepository.findByStatus("COMPLETED");

        assertThat(activeSessions).hasSize(1);
        assertThat(completedSessions).hasSize(1);
    }

    @Test
    public void testFindByLoginTimeBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<Session> sessions = sessionRepository.findByLoginTimeBetween(startTime, endTime);

        assertThat(sessions).hasSize(2);
    }

    @Test
    public void testFindByIpAddress() {
        List<Session> sessions = sessionRepository.findByIpAddress("192.168.1.100");

        assertThat(sessions).hasSize(2);
        assertThat(sessions).allMatch(s -> s.getIpAddress().equals("192.168.1.100"));
    }

    @Test
    public void testFindByDeviceContaining() {
        List<Session> chromeSessions = sessionRepository.findByDeviceContaining("Chrome");
        List<Session> mobileSessions = sessionRepository.findByDeviceContaining("Mobile");

        assertThat(chromeSessions).hasSize(1);
        assertThat(mobileSessions).hasSize(1);
    }

    @Test
    public void testFindByLocationContaining() {
        List<Session> istanbulSessions = sessionRepository.findByLocationContaining("Istanbul");

        assertThat(istanbulSessions).hasSize(2);
    }

    @Test
    public void testFindByLogoutTimeIsNull() {
        List<Session> activeSessions = sessionRepository.findByLogoutTimeIsNull();

        assertThat(activeSessions).hasSize(1);
    }

    @Test
    public void testFindFirstByUserIdOrderByLoginTimeDesc() {
        Optional<Session> latestSession = sessionRepository.findFirstByUserIdOrderByLoginTimeDesc(userId);

        assertThat(latestSession).isPresent();
        assertThat(latestSession.get().getSessionId()).isEqualTo(session2.getSessionId());
    }

    @Test
    public void testSoftDelete() {
        Session session = sessionRepository.findById(session1.getSessionId()).orElseThrow();
        session.setDeletedAt(LocalDateTime.now());
        session.setDeletedBy("testUser");
        sessionRepository.save(session);

        Optional<Session> deletedSession = sessionRepository.findById(session1.getSessionId());
        assertThat(deletedSession).isEmpty();
    }

    @Test
    public void testEndSession() {
        Session session = sessionRepository.findById(session2.getSessionId()).orElseThrow();
        LocalDateTime logoutTime = LocalDateTime.now();
        session.setLogoutTime(logoutTime);
        session.setStatus("COMPLETED");
        sessionRepository.save(session);

        Session endedSession = sessionRepository.findById(session2.getSessionId()).orElseThrow();
        assertThat(endedSession.getStatus()).isEqualTo("COMPLETED");
        assertThat(endedSession.getLogoutTime()).isEqualTo(logoutTime);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
