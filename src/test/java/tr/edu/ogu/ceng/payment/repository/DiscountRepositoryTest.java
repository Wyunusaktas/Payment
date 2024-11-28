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

import tr.edu.ogu.ceng.payment.entity.Discount;

@SpringBootTest
public class DiscountRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private DiscountRepository discountRepository;

    private Discount discount1;
    private Discount discount2;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        discount1 = new Discount();
        discount1.setCode("SUMMER2024");
        discount1.setDiscountAmount(new BigDecimal("20.00"));
        discount1.setDiscountType("PERCENTAGE");
        discount1.setValidFrom(LocalDateTime.now().minusDays(1));
        discount1.setValidTo(LocalDateTime.now().plusDays(30));
        discountRepository.save(discount1);

        discount2 = new Discount();
        discount2.setCode("WELCOME50");
        discount2.setDiscountAmount(new BigDecimal("50.00"));
        discount2.setDiscountType("FIXED");
        discount2.setValidFrom(LocalDateTime.now());
        discount2.setValidTo(LocalDateTime.now().plusDays(7));
        discountRepository.save(discount2);
    }

    @Test
    public void testFindById() {
        Optional<Discount> found = discountRepository.findById(discount1.getDiscountId());

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo(discount1.getCode());
    }

    @Test
    public void testFindByCode() {
        Optional<Discount> found = discountRepository.findByCode("SUMMER2024");

        assertThat(found).isPresent();
        assertThat(found.get().getDiscountType()).isEqualTo("PERCENTAGE");
    }

    @Test
    public void testFindByDiscountType() {
        List<Discount> percentageDiscounts = discountRepository.findByDiscountType("PERCENTAGE");
        List<Discount> fixedDiscounts = discountRepository.findByDiscountType("FIXED");

        assertThat(percentageDiscounts).hasSize(1);
        assertThat(fixedDiscounts).hasSize(1);
    }

    @Test
    public void testFindByValidFromBeforeAndValidToAfter() {
        LocalDateTime now = LocalDateTime.now();
        List<Discount> activeDiscounts = discountRepository.findByValidFromBeforeAndValidToAfter(now, now);

        assertThat(activeDiscounts).hasSize(2);
    }

    @Test
    public void testFindByDiscountAmountGreaterThan() {
        List<Discount> highValueDiscounts = discountRepository.findByDiscountAmountGreaterThan(new BigDecimal("30.00"));

        assertThat(highValueDiscounts).hasSize(1);
        assertThat(highValueDiscounts.get(0).getDiscountAmount()).isGreaterThan(new BigDecimal("30.00"));
    }

    @Test
    public void testFindByValidToAfter() {
        LocalDateTime future = LocalDateTime.now().plusDays(15);
        List<Discount> validDiscounts = discountRepository.findByValidToAfter(future);

        assertThat(validDiscounts).hasSize(1);
        assertThat(validDiscounts.get(0).getCode()).isEqualTo("SUMMER2024");
    }

    @Test
    public void testFindByCodeContaining() {
        List<Discount> summerDiscounts = discountRepository.findByCodeContaining("SUMMER");

        assertThat(summerDiscounts).hasSize(1);
        assertThat(summerDiscounts.get(0).getCode()).contains("SUMMER");
    }

    @Test
    public void testCheckIfDiscountIsValid() {
        LocalDateTime now = LocalDateTime.now();
        List<Discount> validDiscounts = discountRepository.findByValidFromBeforeAndValidToAfterAndDiscountTypeAndDiscountAmountGreaterThan(
                now, now, "PERCENTAGE", new BigDecimal("15.00")
        );

        assertThat(validDiscounts).hasSize(1);
        assertThat(validDiscounts.get(0).getCode()).isEqualTo("SUMMER2024");
    }

    @Test
    public void testSoftDelete() {
        Discount discount = discountRepository.findById(discount1.getDiscountId()).orElseThrow();
        discount.setDeletedAt(LocalDateTime.now());
        discount.setDeletedBy("testUser");
        discountRepository.save(discount);

        Optional<Discount> deletedDiscount = discountRepository.findById(discount1.getDiscountId());
        assertThat(deletedDiscount).isEmpty();
    }

    @Test
    public void testUpdateDiscountValidity() {
        Discount discount = discountRepository.findById(discount1.getDiscountId()).orElseThrow();
        LocalDateTime newValidTo = LocalDateTime.now().plusDays(60);
        discount.setValidTo(newValidTo);
        discountRepository.save(discount);

        Discount updatedDiscount = discountRepository.findById(discount1.getDiscountId()).orElseThrow();
        assertThat(updatedDiscount.getValidTo()).isEqualTo(newValidTo);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
