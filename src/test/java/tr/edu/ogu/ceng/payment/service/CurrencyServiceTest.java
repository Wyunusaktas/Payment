package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.CurrencyDTO;
import tr.edu.ogu.ceng.payment.entity.Currency;
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

    @Autowired
    private ModelMapper modelMapper;

    private Currency currency;
    private CurrencyDTO currencyDTO;

    @BeforeEach
    void setUp() {
        Mockito.reset(currencyRepository);

        currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyName("USD");
        currency.setSymbol("$");
        currency.setExchangeRate(BigDecimal.valueOf(1.00));
        currency.setLastUpdated(LocalDateTime.now());

        currencyDTO = modelMapper.map(currency, CurrencyDTO.class);
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

        CurrencyDTO createdCurrencyDTO = currencyService.save(currencyDTO);

        assertNotNull(createdCurrencyDTO, "Currency creation failed, returned object is null.");
        assertEquals(currencyDTO.getId(), createdCurrencyDTO.getId());
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testFindCurrencyById_NotFound() {
        when(currencyRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<CurrencyDTO> foundCurrencyDTO = currencyService.findById(999L);

        assertFalse(foundCurrencyDTO.isPresent(), "Currency should not be found.");
        verify(currencyRepository, times(1)).findById(999L);
    }

    @Test
    void testFindCurrencyById() {
        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.of(currency));

        Optional<CurrencyDTO> foundCurrencyDTO = currencyService.findById(currency.getId());

        assertTrue(foundCurrencyDTO.isPresent(), "Currency not found.");
        assertEquals(currencyDTO.getId(), foundCurrencyDTO.get().getId());
        verify(currencyRepository, times(1)).findById(currency.getId());
    }

    @Test
    void testUpdateCurrency() {
        currencyDTO.setExchangeRate(BigDecimal.valueOf(1.05));

        // currency nesnesindeki exchangeRate değerini güncelleyin
        currency.setExchangeRate(BigDecimal.valueOf(1.05));

        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyDTO updatedCurrencyDTO = currencyService.save(currencyDTO);

        assertNotNull(updatedCurrencyDTO, "Currency update failed, returned object is null.");
        assertEquals(0, updatedCurrencyDTO.getExchangeRate().compareTo(BigDecimal.valueOf(1.05)), "Exchange rate did not update correctly");
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }



    @Test
    void testSoftDeleteCurrency() {
        ArgumentCaptor<Currency> captor = ArgumentCaptor.forClass(Currency.class);

        when(currencyRepository.findById(currency.getId())).thenReturn(Optional.of(currency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        currencyService.softDelete(currencyDTO.getId(), "testUser");

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

        List<CurrencyDTO> currencyDTOs = currencyService.findAll();

        assertNotNull(currencyDTOs, "Currency list is null.");
        assertFalse(currencyDTOs.isEmpty(), "Currency list is empty.");
        assertEquals(1, currencyDTOs.size(), "Currency list size mismatch.");
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCurrencyWithNegativeExchangeRate() {
        currencyDTO.setExchangeRate(BigDecimal.valueOf(-1.05));

        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyDTO updatedCurrencyDTO = currencyService.save(currencyDTO);

        assertNotNull(updatedCurrencyDTO, "Currency update failed, returned object is null.");
        assertTrue(updatedCurrencyDTO.getExchangeRate().compareTo(BigDecimal.ZERO) >= 0, "Exchange rate should be non-negative");
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

}
