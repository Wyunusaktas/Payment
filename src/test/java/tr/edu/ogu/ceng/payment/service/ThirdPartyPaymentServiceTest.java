package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.model.ThirdPartyPayment;
import tr.edu.ogu.ceng.payment.repository.ThirdPartyPaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class ThirdPartyPaymentServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private ThirdPartyPaymentRepository thirdPartyPaymentRepository;

    @Autowired
    private ThirdPartyPaymentService thirdPartyPaymentService;

    private ThirdPartyPayment thirdPartyPayment;

    @BeforeEach
    void setUp() {
        reset(thirdPartyPaymentRepository);

        thirdPartyPayment = new ThirdPartyPayment();
        thirdPartyPayment.setThirdPartyPaymentId(1L);
        thirdPartyPayment.setProvider("Provider A");
        thirdPartyPayment.setTransactionReference("TXN12345");
        thirdPartyPayment.setStatus("COMPLETED");
        thirdPartyPayment.setProcessedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateThirdPartyPayment() {
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(thirdPartyPayment);

        ThirdPartyPayment createdThirdPartyPayment = thirdPartyPaymentService.save(thirdPartyPayment);

        assertNotNull(createdThirdPartyPayment, "ThirdPartyPayment creation failed, returned object is null.");
        assertEquals(thirdPartyPayment.getProvider(), createdThirdPartyPayment.getProvider());
        verify(thirdPartyPaymentRepository, times(1)).save(any(ThirdPartyPayment.class));
    }

    @Test
    void testFindThirdPartyPaymentById_NotFound() {
        when(thirdPartyPaymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ThirdPartyPayment> foundThirdPartyPayment = thirdPartyPaymentService.findById(999L);

        assertFalse(foundThirdPartyPayment.isPresent(), "ThirdPartyPayment should not be found.");
        verify(thirdPartyPaymentRepository, times(1)).findById(999L);
    }

    @Test
    void testFindThirdPartyPaymentById() {
        when(thirdPartyPaymentRepository.findById(thirdPartyPayment.getThirdPartyPaymentId())).thenReturn(Optional.of(thirdPartyPayment));

        Optional<ThirdPartyPayment> foundThirdPartyPayment = thirdPartyPaymentService.findById(thirdPartyPayment.getThirdPartyPaymentId());

        assertTrue(foundThirdPartyPayment.isPresent(), "ThirdPartyPayment not found.");
        assertEquals(thirdPartyPayment.getProvider(), foundThirdPartyPayment.get().getProvider());
        verify(thirdPartyPaymentRepository, times(1)).findById(thirdPartyPayment.getThirdPartyPaymentId());
    }

    @Test
    void testUpdateThirdPartyPayment() {
        thirdPartyPayment.setStatus("UPDATED");

        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(thirdPartyPayment);

        ThirdPartyPayment updatedThirdPartyPayment = thirdPartyPaymentService.save(thirdPartyPayment);

        assertNotNull(updatedThirdPartyPayment, "ThirdPartyPayment update failed, returned object is null.");
        assertEquals("UPDATED", updatedThirdPartyPayment.getStatus(), "ThirdPartyPayment status did not update correctly.");
        verify(thirdPartyPaymentRepository, times(1)).save(thirdPartyPayment);
    }

    @Test
    void testSoftDeleteThirdPartyPayment() {
        ArgumentCaptor<ThirdPartyPayment> captor = ArgumentCaptor.forClass(ThirdPartyPayment.class);

        when(thirdPartyPaymentRepository.findById(thirdPartyPayment.getThirdPartyPaymentId())).thenReturn(Optional.of(thirdPartyPayment));
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(thirdPartyPayment);

        thirdPartyPaymentService.softDelete(thirdPartyPayment.getThirdPartyPaymentId(), "testUser");

        verify(thirdPartyPaymentRepository, times(1)).findById(thirdPartyPayment.getThirdPartyPaymentId());
        verify(thirdPartyPaymentRepository, times(1)).save(captor.capture());

        ThirdPartyPayment softDeletedThirdPartyPayment = captor.getValue();
        assertNotNull(softDeletedThirdPartyPayment.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedThirdPartyPayment.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteThirdPartyPayment_NotFound() {
        when(thirdPartyPaymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        thirdPartyPaymentService.softDelete(999L, "testUser");

        verify(thirdPartyPaymentRepository, times(1)).findById(999L);
        verify(thirdPartyPaymentRepository, never()).save(any(ThirdPartyPayment.class));
    }

    @Test
    void testFindAllThirdPartyPayments() {
        when(thirdPartyPaymentRepository.findAll()).thenReturn(List.of(thirdPartyPayment));

        List<ThirdPartyPayment> thirdPartyPaymentList = thirdPartyPaymentService.findAll();

        assertNotNull(thirdPartyPaymentList, "ThirdPartyPayment list is null.");
        assertFalse(thirdPartyPaymentList.isEmpty(), "ThirdPartyPayment list is empty.");
        assertEquals(1, thirdPartyPaymentList.size(), "ThirdPartyPayment list size mismatch.");
        verify(thirdPartyPaymentRepository, times(1)).findAll();
    }

    @Test
    void testSaveThirdPartyPayment() {
        ThirdPartyPayment payment = new ThirdPartyPayment();
        payment.setProvider("PayPal");
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(payment);

        ThirdPartyPayment result = thirdPartyPaymentService.save(payment);

        assertNotNull(result, "Saved third-party payment should not be null");
        assertEquals("PayPal", result.getProvider(), "Provider should match");
    }

}
