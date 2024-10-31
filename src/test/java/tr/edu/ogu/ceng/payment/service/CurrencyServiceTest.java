package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.model.Currency;
import tr.edu.ogu.ceng.payment.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class CurrencyServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private CurrencyRepository currencyRepository;

    @Autowired
    private CurrencyService currencyService;

    private Currency currency;

    @BeforeEach
    void setUp() {
        Mockito.reset(currencyRepository);

        currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyName("USD");
        currency.setSymbol("$");
        currency.setExchangeRate(BigDecimal.valueOf(1.00));
        currency.setLastUpdated(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateCurrency() {
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        Currency createdCurrency = currencyService.save(currency);

        assertNotNull(createdCurrency, "Currency creation failed, returned object is null.");
        assertEquals(currency.getId(), createdCurrency.getId());
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testFindCurrencyById_NotFound() {
        when(currencyRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Currency> foundCurrency = currencyService.findById(999L);

        assertFalse(foundCurrency.isPresent(), "Currency should not be found.");
        verify(currencyRepository, times(1)).findById(999L);
    }

    @Test
    void testFindCurrencyById() {
        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.of(currency));

        Optional<Currency> foundCurrency = currencyService.findById(currency.getId());

        assertTrue(foundCurrency.isPresent(), "Currency not found.");
        assertEquals(currency.getId(), foundCurrency.get().getId());
        verify(currencyRepository, times(1)).findById(currency.getId());
    }

    @Test
    void testUpdateCurrency() {
        currency.setExchangeRate(BigDecimal.valueOf(1.05));

        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        Currency updatedCurrency = currencyService.save(currency);

        assertNotNull(updatedCurrency, "Currency update failed, returned object is null.");
        assertEquals(BigDecimal.valueOf(1.05), updatedCurrency.getExchangeRate());
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test
    void testSoftDeleteCurrency() {
        ArgumentCaptor<Currency> captor = ArgumentCaptor.forClass(Currency.class);

        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.of(currency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        currencyService.softDelete(currency.getId(), "testUser");

        verify(currencyRepository, times(1)).findById(currency.getId());
        verify(currencyRepository, times(1)).save(captor.capture());

        Currency softDeletedCurrency = captor.getValue();
        assertNotNull(softDeletedCurrency.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedCurrency.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteCurrency_NotFound() {
        when(currencyRepository.findById(anyLong())).thenReturn(Optional.empty());

        currencyService.softDelete(999L, "testUser");

        verify(currencyRepository, times(1)).findById(999L);
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void testFindAllCurrencies() {
        when(currencyRepository.findAll()).thenReturn(List.of(currency));

        List<Currency> currencies = currencyService.findAll();

        assertNotNull(currencies, "Currency list is null.");
        assertFalse(currencies.isEmpty(), "Currency list is empty.");
        assertEquals(1, currencies.size(), "Currency list size mismatch.");
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void testSoftDeleteUpdatesDeletedAt() {
        Long currencyId = 1L;
        Currency currency = new Currency();
        when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(currency));

        currencyService.softDelete(currencyId, "testUser");

        assertNotNull(currency.getDeletedAt(), "Deleted currency should have a non-null deletedAt field");
        assertEquals("testUser", currency.getDeletedBy());
    }

}
