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
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.AuditLog;

@SpringBootTest
public class AuditLogRepositoryTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog auditLog1;
    private AuditLog auditLog2;
    private UUID userId;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        auditLog1 = new AuditLog();
        auditLog1.setUserId(userId);
        auditLog1.setActionType("PAYMENT_CREATED");
        auditLog1.setTimestamp(LocalDateTime.now().minusHours(1));
        auditLog1.setDescription("Payment created for order #12345");
        auditLogRepository.save(auditLog1);

        auditLog2 = new AuditLog();
        auditLog2.setUserId(userId);
        auditLog2.setActionType("PAYMENT_COMPLETED");
        auditLog2.setTimestamp(LocalDateTime.now());
        auditLog2.setDescription("Payment successfully completed");
        auditLogRepository.save(auditLog2);
    }

    @Test
    public void testFindById() {
        Optional<AuditLog> found = auditLogRepository.findById(auditLog1.getLogId());

        assertThat(found).isPresent();
        assertThat(found.get().getActionType()).isEqualTo(auditLog1.getActionType());
    }

    @Test
    public void testFindByUserId() {
        List<AuditLog> logs = auditLogRepository.findByUserId(userId);

        assertThat(logs).hasSize(2);
        assertThat(logs).allMatch(log -> log.getUserId().equals(userId));
    }

    @Test
    public void testFindByActionType() {
        List<AuditLog> createdLogs = auditLogRepository.findByActionType("PAYMENT_CREATED");
        List<AuditLog> completedLogs = auditLogRepository.findByActionType("PAYMENT_COMPLETED");

        assertThat(createdLogs).hasSize(1);
        assertThat(completedLogs).hasSize(1);
    }

    @Test
    public void testFindByTimestampBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<AuditLog> logs = auditLogRepository.findByTimestampBetween(startTime, endTime);

        assertThat(logs).hasSize(2);
    }

    @Test
    public void testFindByDescriptionContaining() {
        List<AuditLog> logs = auditLogRepository.findByDescriptionContaining("successfully");

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getDescription()).contains("successfully");
    }

    @Query
    @Test
    public void testFindAll() {
        List<AuditLog> logs = auditLogRepository.findAll();

        assertThat(logs).isNotEmpty();
        assertThat(logs.size()).isGreaterThanOrEqualTo(2);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
