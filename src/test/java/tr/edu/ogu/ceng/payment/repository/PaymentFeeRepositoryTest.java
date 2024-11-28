package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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

import tr.edu.ogu.ceng.payment.entity.Currency;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.PaymentFee;

@SpringBootTest
public class PaymentFeeRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PaymentFeeRepository paymentFeeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private PaymentFee fee1;
    private PaymentFee fee2;
    private Payment payment;
    private Currency currency;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        currency = new Currency();
        currency.setCurrencyName("USD");
        currency.setSymbol("$");
        currency.setExchangeRate(BigDecimal.ONE);
        currencyRepository.save(currency);

        payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setStatus("COMPLETED");
        payment.setCurrency(currency);
        paymentRepository.save(payment);

        fee1 = new PaymentFee();
        fee1.setPayment(payment);
        fee1.setFeeType("PROCESSING_FEE");
        fee1.setAmount(new BigDecimal("10.00"));
        fee1.setCurrency(currency);
        fee1.setCreatedAt(LocalDateTime.now().minusHours(1));
        paymentFeeRepository.save(fee1);

        fee2 = new PaymentFee();
        fee2.setPayment(payment);
        fee2.setFeeType("CURRENCY_CONVERSION_FEE");
        fee2.setAmount(new BigDecimal("5.00"));
        fee2.setCurrency(currency);
        fee2.setCreatedAt(LocalDateTime.now());
        paymentFeeRepository.save(fee2);
    }

    @Test
    public void testFindById() {
        Optional<PaymentFee> found = paymentFeeRepository.findById(fee1.getFeeId());

        assertThat(found).isPresent();
        assertThat(found.get().getFeeType()).isEqualTo(fee1.getFeeType());
    }

    @Test
    public void testFindByPaymentPaymentId() {
        List<PaymentFee> fees = paymentFeeRepository.findByPaymentPaymentId(payment.getPaymentId());

        assertThat(fees).hasSize(2);
        assertThat(fees).allMatch(f -> f.getPayment().equals(payment));
    }

    @Test
    public void testFindByFeeType() {
        List<PaymentFee> processingFees = paymentFeeRepository.findByFeeType("PROCESSING_FEE");
        List<PaymentFee> conversionFees = paymentFeeRepository.findByFeeType("CURRENCY_CONVERSION_FEE");

        assertThat(processingFees).hasSize(1);
        assertThat(conversionFees).hasSize(1);
    }

    @Test
    public void testFindByAmountGreaterThan() {
        List<PaymentFee> highFees = paymentFeeRepository.findByAmountGreaterThan(new BigDecimal("7.50"));

        assertThat(highFees).hasSize(1);
        assertThat(highFees.get(0).getAmount()).isGreaterThan(new BigDecimal("7.50"));
    }

    @Test
    public void testFindByCurrencyId() {
        List<PaymentFee> fees = paymentFeeRepository.findByCurrencyId(currency.getId());

        assertThat(fees).hasSize(2);
    }

    @Test
    public void testFindByCreatedAtBetween() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        List<PaymentFee> fees = paymentFeeRepository.findByCreatedAtBetween(startTime, endTime);

        assertThat(fees).hasSize(2);
    }

    @Test
    public void testFindFirstByOrderByCreatedAtDesc() {
        Optional<PaymentFee> latestFee = paymentFeeRepository.findFirstByOrderByCreatedAtDesc();

        assertThat(latestFee).isPresent();
        assertThat(latestFee.get().getFeeType()).isEqualTo(fee2.getFeeType());
    }

    @Test
    public void testFindByPaymentPaymentIdAndFeeType() {
        List<PaymentFee> processingFees = paymentFeeRepository.findByPaymentPaymentIdAndFeeType(payment.getPaymentId(), "PROCESSING_FEE");

        assertThat(processingFees).hasSize(1);
    }

    @Test
    public void testSoftDelete() {
        PaymentFee fee = paymentFeeRepository.findById(fee1.getFeeId()).orElseThrow();
        fee.setDeletedAt(LocalDateTime.now());
        fee.setDeletedBy("testUser");
        paymentFeeRepository.save(fee);

        Optional<PaymentFee> deletedFee = paymentFeeRepository.findById(fee1.getFeeId());
        assertThat(deletedFee).isEmpty();
    }

    @Test
    public void testUpdateFeeAmount() {
        PaymentFee fee = paymentFeeRepository.findById(fee1.getFeeId()).orElseThrow();
        BigDecimal newAmount = new BigDecimal("15.00");
        fee.setAmount(newAmount);
        paymentFeeRepository.save(fee);

        PaymentFee updatedFee = paymentFeeRepository.findById(fee1.getFeeId()).orElseThrow();
        assertThat(updatedFee.getAmount()).isEqualTo(newAmount);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
