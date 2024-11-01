package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.PaymentAnalyticsDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentAnalytics;
import tr.edu.ogu.ceng.payment.repository.PaymentAnalyticsRepository;

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
public class PaymentAnalyticsServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private PaymentAnalyticsRepository paymentAnalyticsRepository;

    @Autowired
    private PaymentAnalyticsService paymentAnalyticsService;

    @Autowired
    private ModelMapper modelMapper;

    private PaymentAnalytics paymentAnalytics;
    private PaymentAnalyticsDTO paymentAnalyticsDTO;

    @BeforeEach
    void setUp() {
        reset(paymentAnalyticsRepository);

        paymentAnalytics = new PaymentAnalytics();
        paymentAnalytics.setAnalyticsId(1L);
        paymentAnalytics.setTotalPayments(new BigDecimal("1000.50"));
        paymentAnalytics.setTotalRefunds(new BigDecimal("100.00"));
        paymentAnalytics.setAverageTransactionValue(new BigDecimal("200.10"));
        paymentAnalytics.setPaymentChannel("Online");
        paymentAnalytics.setReportingDate(LocalDateTime.now());

        paymentAnalyticsDTO = modelMapper.map(paymentAnalytics, PaymentAnalyticsDTO.class);
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePaymentAnalytics() {
        when(paymentAnalyticsRepository.save(any(PaymentAnalytics.class))).thenReturn(paymentAnalytics);

        PaymentAnalyticsDTO createdAnalyticsDTO = paymentAnalyticsService.save(paymentAnalyticsDTO);

        assertNotNull(createdAnalyticsDTO, "PaymentAnalytics creation failed, returned object is null.");
        assertEquals(paymentAnalyticsDTO.getAnalyticsId(), createdAnalyticsDTO.getAnalyticsId());
        verify(paymentAnalyticsRepository, times(1)).save(any(PaymentAnalytics.class));
    }

    @Test
    void testFindPaymentAnalyticsById_NotFound() {
        when(paymentAnalyticsRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentAnalyticsDTO> foundAnalyticsDTO = paymentAnalyticsService.findById(999L);

        assertFalse(foundAnalyticsDTO.isPresent(), "PaymentAnalytics should not be found.");
        verify(paymentAnalyticsRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentAnalyticsById() {
        when(paymentAnalyticsRepository.findById(paymentAnalytics.getAnalyticsId())).thenReturn(Optional.of(paymentAnalytics));

        Optional<PaymentAnalyticsDTO> foundAnalyticsDTO = paymentAnalyticsService.findById(paymentAnalyticsDTO.getAnalyticsId());

        assertTrue(foundAnalyticsDTO.isPresent(), "PaymentAnalytics not found.");
        assertEquals(paymentAnalyticsDTO.getAnalyticsId(), foundAnalyticsDTO.get().getAnalyticsId());
        verify(paymentAnalyticsRepository, times(1)).findById(paymentAnalyticsDTO.getAnalyticsId());
    }

    @Test
    void testUpdatePaymentAnalytics() {
        paymentAnalyticsDTO.setPaymentChannel("In-Store");

        PaymentAnalytics updatedPaymentAnalytics = modelMapper.map(paymentAnalyticsDTO, PaymentAnalytics.class);
        when(paymentAnalyticsRepository.save(any(PaymentAnalytics.class))).thenReturn(updatedPaymentAnalytics);

        PaymentAnalyticsDTO updatedAnalyticsDTO = paymentAnalyticsService.save(paymentAnalyticsDTO);

        assertNotNull(updatedAnalyticsDTO, "PaymentAnalytics update failed, returned object is null.");
        assertEquals("In-Store", updatedAnalyticsDTO.getPaymentChannel(), "Payment channel did not update correctly.");
        verify(paymentAnalyticsRepository, times(1)).save(any(PaymentAnalytics.class));
    }


    @Test
    void testSoftDeletePaymentAnalytics() {
        ArgumentCaptor<PaymentAnalytics> captor = ArgumentCaptor.forClass(PaymentAnalytics.class);

        when(paymentAnalyticsRepository.findById(paymentAnalytics.getAnalyticsId())).thenReturn(Optional.of(paymentAnalytics));
        when(paymentAnalyticsRepository.save(any(PaymentAnalytics.class))).thenReturn(paymentAnalytics);

        paymentAnalyticsService.softDelete(paymentAnalyticsDTO.getAnalyticsId(), "testUser");

        verify(paymentAnalyticsRepository, times(1)).findById(paymentAnalyticsDTO.getAnalyticsId());
        verify(paymentAnalyticsRepository, times(1)).save(captor.capture());

        PaymentAnalytics softDeletedAnalytics = captor.getValue();
        assertNotNull(softDeletedAnalytics.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedAnalytics.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentAnalytics_NotFound() {
        when(paymentAnalyticsRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentAnalyticsService.softDelete(999L, "testUser");

        verify(paymentAnalyticsRepository, times(1)).findById(999L);
        verify(paymentAnalyticsRepository, never()).save(any(PaymentAnalytics.class));
    }

    @Test
    void testFindAllPaymentAnalytics() {
        when(paymentAnalyticsRepository.findAll()).thenReturn(List.of(paymentAnalytics));

        List<PaymentAnalyticsDTO> analyticsDTOList = paymentAnalyticsService.findAll();

        assertNotNull(analyticsDTOList, "PaymentAnalytics list is null.");
        assertFalse(analyticsDTOList.isEmpty(), "PaymentAnalytics list is empty.");
        assertEquals(1, analyticsDTOList.size(), "PaymentAnalytics list size mismatch.");
        verify(paymentAnalyticsRepository, times(1)).findAll();
    }

    @Test
    void testSavePaymentAnalytics() {
        PaymentAnalyticsDTO analyticsDTO = new PaymentAnalyticsDTO();
        analyticsDTO.setTotalPayments(BigDecimal.valueOf(500.0));

        // paymentAnalytics nesnesinin totalPayments alanını güncelleyerek uyumlu hale getirin
        paymentAnalytics.setTotalPayments(BigDecimal.valueOf(500.0));

        when(paymentAnalyticsRepository.save(any(PaymentAnalytics.class))).thenReturn(paymentAnalytics);

        PaymentAnalyticsDTO result = paymentAnalyticsService.save(analyticsDTO);

        assertNotNull(result, "Saved PaymentAnalyticsDTO should not be null");
        assertEquals(BigDecimal.valueOf(500.0), result.getTotalPayments(), "Total payments should match");
    }

    @Test
    void testSavePaymentAnalyticsWithZeroTotalPayments() {
        // paymentAnalytics nesnesinin totalPayments alanını sıfır olarak ayarlayın
        paymentAnalytics.setTotalPayments(BigDecimal.ZERO);
        PaymentAnalyticsDTO analyticsDTO = new PaymentAnalyticsDTO();
        analyticsDTO.setTotalPayments(BigDecimal.ZERO);

        // Repository save işleminde sıfır olarak ayarlanmış paymentAnalytics nesnesini döndür
        when(paymentAnalyticsRepository.save(any(PaymentAnalytics.class))).thenReturn(paymentAnalytics);

        PaymentAnalyticsDTO result = paymentAnalyticsService.save(analyticsDTO);

        assertNotNull(result, "Saved PaymentAnalyticsDTO should not be null");
        assertEquals(0, result.getTotalPayments().compareTo(BigDecimal.ZERO), "Total payments should be zero");
        verify(paymentAnalyticsRepository, times(1)).save(any(PaymentAnalytics.class));
    }




}
