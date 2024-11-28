package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.ErrorLog;

@SpringBootTest
public class ErrorLogRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ErrorLogRepository errorLogRepository;

    private ErrorLog errorLog1;
    private ErrorLog errorLog2;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        errorLog1 = new ErrorLog();
        errorLog1.setErrorMessage("Database connection failed");
        errorLog1.setStackTrace("java.sql.SQLException: Connection refused");
        errorLog1.setOccurredAt(LocalDateTime.now().minusHours(2));
        errorLog1.setResolved(false);
        errorLogRepository.save(errorLog1);

        errorLog2 = new ErrorLog();
        errorLog2.setErrorMessage("Payment validation error");
        errorLog2.setStackTrace("javax.validation.ValidationException: Invalid amount");
        errorLog2.setOccurredAt(LocalDateTime.now().minusHours(1));
        errorLog2.setResolved(true);
        errorLog2.setResolvedAt(LocalDateTime.now());
        errorLogRepository.save(errorLog2);
    }

    @Test
    public void testFindById() {
        Optional<ErrorLog> found = errorLogRepository.findById(errorLog1.getErrorId());

        assertThat(found).isPresent();
        assertThat(found.get().getErrorMessage()).isEqualTo(errorLog1.getErrorMessage());
    }

    @Test
    public void testFindByResolved() {
        List<ErrorLog> unresolvedErrors = errorLogRepository.findByResolved(false);
        List<ErrorLog> resolvedErrors = errorLogRepository.findByResolved(true);

        assertThat(unresolvedErrors).hasSize(1);
        assertThat(resolvedErrors).hasSize(1);
    }

    @Test
    public void testFindByOccurredAtBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now();

        List<ErrorLog> logs = errorLogRepository.findByOccurredAtBetween(startTime, endTime);

        assertThat(logs).hasSize(2);
    }

    @Test
    public void testFindByErrorMessageContaining() {
        List<ErrorLog> databaseErrors = errorLogRepository.findByErrorMessageContaining("Database");
        List<ErrorLog> validationErrors = errorLogRepository.findByErrorMessageContaining("validation");

        assertThat(databaseErrors).hasSize(1);
        assertThat(validationErrors).hasSize(1);
    }

    @Test
    public void testFindByStackTraceContaining() {
        List<ErrorLog> sqlErrors = errorLogRepository.findByStackTraceContaining("SQLException");

        assertThat(sqlErrors).hasSize(1);
    }

    @Test
    public void testFindByResolvedAtIsNull() {
        List<ErrorLog> unresolvedErrors = errorLogRepository.findByResolvedAtIsNull();

        assertThat(unresolvedErrors).hasSize(1);
    }

    @Test
    public void testFindByResolvedAtIsNotNull() {
        List<ErrorLog> resolvedErrors = errorLogRepository.findByResolvedAtIsNotNull();

        assertThat(resolvedErrors).hasSize(1);
    }

    @Test
    public void testFindFirstByOrderByOccurredAtDesc() {
        Optional<ErrorLog> latestError = errorLogRepository.findFirstByOrderByOccurredAtDesc();

        assertThat(latestError).isPresent();
        assertThat(latestError.get().getErrorMessage()).isEqualTo(errorLog2.getErrorMessage());
    }

    @Test
    public void testSoftDelete() {
        ErrorLog errorLog = errorLogRepository.findById(errorLog1.getErrorId()).orElseThrow();
        errorLog.setDeletedAt(LocalDateTime.now());
        errorLog.setDeletedBy("testUser");
        errorLogRepository.save(errorLog);

        Optional<ErrorLog> deletedError = errorLogRepository.findById(errorLog1.getErrorId());
        assertThat(deletedError).isEmpty();
    }

    @Test
    public void testResolveError() {
        ErrorLog errorLog = errorLogRepository.findById(errorLog1.getErrorId()).orElseThrow();
        errorLog.setResolved(true);
        errorLog.setResolvedAt(LocalDateTime.now());
        errorLogRepository.save(errorLog);

        ErrorLog resolvedError = errorLogRepository.findById(errorLog1.getErrorId()).orElseThrow();
        assertThat(resolvedError.isResolved()).isTrue();
        assertThat(resolvedError.getResolvedAt()).isNotNull();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
