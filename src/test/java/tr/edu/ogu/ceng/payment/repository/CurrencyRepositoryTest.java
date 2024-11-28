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

import tr.edu.ogu.ceng.payment.entity.Currency;

@SpringBootTest
public class CurrencyRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CurrencyRepository currencyRepository;

    private Currency currency1;
    private Currency currency2;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        currency1 = new Currency();
        currency1.setCurrencyName("US Dollar");
        currency1.setSymbol("USD");
        currency1.setExchangeRate(BigDecimal.ONE);
        currency1.setLastUpdated(LocalDateTime.now().minusHours(1));
        currencyRepository.save(currency1);

        currency2 = new Currency();
        currency2.setCurrencyName("Euro");
        currency2.setSymbol("EUR");
        currency2.setExchangeRate(new BigDecimal("1.20"));
        currency2.setLastUpdated(LocalDateTime.now());
        currencyRepository.save(currency2);
    }

    @Test
    public void testFindById() {
        Optional<Currency> found = currencyRepository.findById(currency1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSymbol()).isEqualTo(currency1.getSymbol());
    }

    @Test
    public void testFindByCurrencyName() {
        List<Currency> usDollars = currencyRepository.findByCurrencyName("US Dollar");
        List<Currency> euros = currencyRepository.findByCurrencyName("Euro");

        assertThat(usDollars).hasSize(1);
        assertThat(euros).hasSize(1);
        assertThat(usDollars.get(0).getSymbol()).isEqualTo("USD");
        assertThat(euros.get(0).getSymbol()).isEqualTo("EUR");
    }

    @Test
    public void testFindBySymbol() {
        Optional<Currency> usd = currencyRepository.findBySymbol("USD");
        Optional<Currency> eur = currencyRepository.findBySymbol("EUR");

        assertThat(usd).isPresent();
        assertThat(eur).isPresent();
        assertThat(usd.get().getCurrencyName()).isEqualTo("US Dollar");
        assertThat(eur.get().getCurrencyName()).isEqualTo("Euro");
    }

    @Test
    public void testFindByExchangeRateGreaterThan() {
        List<Currency> highRateCurrencies = currencyRepository.findByExchangeRateGreaterThan(new BigDecimal("1.10"));

        assertThat(highRateCurrencies).hasSize(1);
        assertThat(highRateCurrencies.get(0).getExchangeRate()).isGreaterThan(new BigDecimal("1.10"));
    }

    @Test
    public void testFindByLastUpdatedAfter() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(2);
        List<Currency> recentlyUpdated = currencyRepository.findByLastUpdatedAfter(oneHourAgo);

        assertThat(recentlyUpdated).hasSize(2);
    }

    @Test
    public void testFindFirstByOrderByLastUpdatedDesc() {
        Optional<Currency> mostRecent = currencyRepository.findFirstByOrderByLastUpdatedDesc();

        assertThat(mostRecent).isPresent();
        assertThat(mostRecent.get().getSymbol()).isEqualTo(currency2.getSymbol());
    }

    @Test
    public void testFindByExchangeRateBetween() {
        List<Currency> currencies = currencyRepository.findByExchangeRateBetween(
                new BigDecimal("0.90"),
                new BigDecimal("1.30")
        );

        assertThat(currencies).hasSize(2);
    }

    @Test
    public void testSoftDelete() {
        Currency currency = currencyRepository.findById(currency1.getId()).orElseThrow();
        currency.setDeletedAt(LocalDateTime.now());
        currency.setDeletedBy("testUser");
        currencyRepository.save(currency);

        Optional<Currency> deletedCurrency = currencyRepository.findById(currency1.getId());
        assertThat(deletedCurrency).isEmpty();
    }

    @Test
    public void testUpdateExchangeRate() {
        Currency currency = currencyRepository.findById(currency1.getId()).orElseThrow();
        BigDecimal newRate = new BigDecimal("1.05");
        currency.setExchangeRate(newRate);
        currency.setLastUpdated(LocalDateTime.now());
        currencyRepository.save(currency);

        Currency updatedCurrency = currencyRepository.findById(currency1.getId()).orElseThrow();
        assertThat(updatedCurrency.getExchangeRate()).isEqualTo(newRate);
        assertThat(updatedCurrency.getLastUpdated()).isAfter(currency1.getLastUpdated());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
