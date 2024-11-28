package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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

import tr.edu.ogu.ceng.payment.entity.PaymentAnalytics;

@SpringBootTest
public class PaymentAnalyticsRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentAnalyticsRepository paymentAnalyticsRepository;

    private PaymentAnalytics analytics1;
    private PaymentAnalytics analytics2;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        analytics1 = new PaymentAnalytics();
        analytics1.setTotalPayments(new BigDecimal("10000.00"));
        analytics1.setTotalRefunds(new BigDecimal("500.00"));
        analytics1.setAverageTransactionValue(new BigDecimal("250.00"));
        analytics1.setPaymentChannel("CREDIT_CARD");
        analytics1.setReportingDate(LocalDateTime.now().minusDays(1));
        paymentAnalyticsRepository.save(analytics1);

        analytics2 = new PaymentAnalytics();
        analytics2.setTotalPayments(new BigDecimal("15000.00"));
        analytics2.setTotalRefunds(new BigDecimal("750.00"));
        analytics2.setAverageTransactionValue(new BigDecimal("300.00"));
        analytics2.setPaymentChannel("BANK_TRANSFER");
        analytics2.setReportingDate(LocalDateTime.now());
        paymentAnalyticsRepository.save(analytics2);
    }

    @Test
    public void testFindById() {
        Optional<PaymentAnalytics> found = paymentAnalyticsRepository.findById(analytics1.getAnalyticsId());

        assertThat(found).isPresent();
        assertThat(found.get().getPaymentChannel()).isEqualTo(analytics1.getPaymentChannel());
    }

    @Test
    public void testFindByPaymentChannel() {
        List<PaymentAnalytics> creditCardAnalytics = paymentAnalyticsRepository.findByPaymentChannel("CREDIT_CARD");
        List<PaymentAnalytics> bankTransferAnalytics = paymentAnalyticsRepository.findByPaymentChannel("BANK_TRANSFER");

        assertThat(creditCardAnalytics).hasSize(1);
        assertThat(bankTransferAnalytics).hasSize(1);
    }

    @Test
    public void testFindByReportingDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<PaymentAnalytics> analyticsList = paymentAnalyticsRepository.findByReportingDateBetween(startDate, endDate);

        assertThat(analyticsList).hasSize(2);
    }

    @Test
    public void testFindByTotalPaymentsGreaterThan() {
        List<PaymentAnalytics> highVolumeAnalytics = paymentAnalyticsRepository.findByTotalPaymentsGreaterThan(new BigDecimal("12000.00"));

        assertThat(highVolumeAnalytics).hasSize(1);
        assertThat(highVolumeAnalytics.get(0).getTotalPayments()).isGreaterThan(new BigDecimal("12000.00"));
    }

    @Test
    public void testFindByAverageTransactionValueGreaterThan() {
        List<PaymentAnalytics> highValueAnalytics = paymentAnalyticsRepository.findByAverageTransactionValueGreaterThan(new BigDecimal("275.00"));

        assertThat(highValueAnalytics).hasSize(1);
        assertThat(highValueAnalytics.get(0).getAverageTransactionValue()).isGreaterThan(new BigDecimal("275.00"));
    }

    @Test
    public void testFindTopByOrderByReportingDateDesc() {
        Optional<PaymentAnalytics> latestAnalytics = paymentAnalyticsRepository.findFirstByOrderByReportingDateDesc();

        assertThat(latestAnalytics).isPresent();
        assertThat(latestAnalytics.get().getReportingDate()).isEqualTo(analytics2.getReportingDate());
    }

    @Test
    public void testCalculateTotalRefundsByChannel() {
        BigDecimal totalCreditCardRefunds = paymentAnalyticsRepository.findByPaymentChannel("CREDIT_CARD")
                .stream()
                .map(PaymentAnalytics::getTotalRefunds)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalCreditCardRefunds).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    public void testSoftDelete() {
        PaymentAnalytics analytics = paymentAnalyticsRepository.findById(analytics1.getAnalyticsId()).orElseThrow();
        analytics.setDeletedAt(LocalDateTime.now());
        analytics.setDeletedBy("testUser");
        paymentAnalyticsRepository.save(analytics);

        Optional<PaymentAnalytics> deletedAnalytics = paymentAnalyticsRepository.findById(analytics1.getAnalyticsId());
        assertThat(deletedAnalytics).isEmpty();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
