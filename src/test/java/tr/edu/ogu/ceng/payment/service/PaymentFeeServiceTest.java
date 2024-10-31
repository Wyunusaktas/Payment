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
import tr.edu.ogu.ceng.payment.model.PaymentFee;
import tr.edu.ogu.ceng.payment.repository.PaymentFeeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class PaymentFeeServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private PaymentFeeRepository paymentFeeRepository;

    @Autowired
    private PaymentFeeService paymentFeeService;

    private PaymentFee paymentFee;

    @BeforeEach
    void setUp() {
        reset(paymentFeeRepository);

        paymentFee = new PaymentFee();
        paymentFee.setFeeId(1L);
        paymentFee.setFeeType("Transaction Fee");
        paymentFee.setAmount(new BigDecimal("5.00"));
        paymentFee.setCreatedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePaymentFee() {
        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(paymentFee);

        PaymentFee createdFee = paymentFeeService.save(paymentFee);

        assertNotNull(createdFee, "PaymentFee creation failed, returned object is null.");
        assertEquals(paymentFee.getFeeId(), createdFee.getFeeId());
        verify(paymentFeeRepository, times(1)).save(any(PaymentFee.class));
    }

    @Test
    void testFindPaymentFeeById_NotFound() {
        when(paymentFeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentFee> foundFee = paymentFeeService.findById(999L);

        assertFalse(foundFee.isPresent(), "PaymentFee should not be found.");
        verify(paymentFeeRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentFeeById() {
        when(paymentFeeRepository.findById(paymentFee.getFeeId())).thenReturn(Optional.of(paymentFee));

        Optional<PaymentFee> foundFee = paymentFeeService.findById(paymentFee.getFeeId());

        assertTrue(foundFee.isPresent(), "PaymentFee not found.");
        assertEquals(paymentFee.getFeeId(), foundFee.get().getFeeId());
        verify(paymentFeeRepository, times(1)).findById(paymentFee.getFeeId());
    }

    @Test
    void testUpdatePaymentFee() {
        paymentFee.setAmount(new BigDecimal("10.00"));

        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(paymentFee);

        PaymentFee updatedFee = paymentFeeService.save(paymentFee);

        assertNotNull(updatedFee, "PaymentFee update failed, returned object is null.");
        assertEquals(new BigDecimal("10.00"), updatedFee.getAmount(), "Fee amount did not update correctly.");
        verify(paymentFeeRepository, times(1)).save(paymentFee);
    }

    @Test
    void testSoftDeletePaymentFee() {
        ArgumentCaptor<PaymentFee> captor = ArgumentCaptor.forClass(PaymentFee.class);

        when(paymentFeeRepository.findById(paymentFee.getFeeId())).thenReturn(Optional.of(paymentFee));
        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(paymentFee);

        paymentFeeService.softDelete(paymentFee.getFeeId(), "testUser");

        verify(paymentFeeRepository, times(1)).findById(paymentFee.getFeeId());
        verify(paymentFeeRepository, times(1)).save(captor.capture());

        PaymentFee softDeletedFee = captor.getValue();
        assertNotNull(softDeletedFee.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedFee.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentFee_NotFound() {
        when(paymentFeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentFeeService.softDelete(999L, "testUser");

        verify(paymentFeeRepository, times(1)).findById(999L);
        verify(paymentFeeRepository, never()).save(any(PaymentFee.class));
    }

    @Test
    void testFindAllPaymentFees() {
        when(paymentFeeRepository.findAll()).thenReturn(List.of(paymentFee));

        List<PaymentFee> feesList = paymentFeeService.findAll();

        assertNotNull(feesList, "PaymentFee list is null.");
        assertFalse(feesList.isEmpty(), "PaymentFee list is empty.");
        assertEquals(1, feesList.size(), "PaymentFee list size mismatch.");
        verify(paymentFeeRepository, times(1)).findAll();
    }

    @Test
    void testFindAllPaymentFeesEmptyList() {
        when(paymentFeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<PaymentFee> fees = paymentFeeService.findAll();

        assertTrue(fees.isEmpty(), "findAll should return an empty list if no payment fees exist");
    }

}
