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
import tr.edu.ogu.ceng.payment.dto.PaymentFeeDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentFee;
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

    @Autowired
    private ModelMapper modelMapper;

    private PaymentFeeDTO paymentFeeDTO;

    @BeforeEach
    void setUp() {
        reset(paymentFeeRepository);

        paymentFeeDTO = new PaymentFeeDTO();
        paymentFeeDTO.setFeeId(1L);
        paymentFeeDTO.setFeeType("Transaction Fee");
        paymentFeeDTO.setAmount(BigDecimal.valueOf(5.0));
        paymentFeeDTO.setCreatedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreatePaymentFee() {
        PaymentFee paymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);
        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(paymentFee);

        PaymentFeeDTO createdFeeDTO = paymentFeeService.save(paymentFeeDTO);

        assertNotNull(createdFeeDTO, "PaymentFee creation failed, returned object is null.");
        assertEquals(paymentFeeDTO.getFeeId(), createdFeeDTO.getFeeId());
        verify(paymentFeeRepository, times(1)).save(any(PaymentFee.class));
    }

    @Test
    void testFindPaymentFeeById_NotFound() {
        when(paymentFeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentFeeDTO> foundFeeDTO = paymentFeeService.findById(999L);

        assertFalse(foundFeeDTO.isPresent(), "PaymentFee should not be found.");
        verify(paymentFeeRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentFeeById() {
        PaymentFee paymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);
        when(paymentFeeRepository.findById(paymentFeeDTO.getFeeId())).thenReturn(Optional.of(paymentFee));

        Optional<PaymentFeeDTO> foundFeeDTO = paymentFeeService.findById(paymentFeeDTO.getFeeId());

        assertTrue(foundFeeDTO.isPresent(), "PaymentFee not found.");
        assertEquals(paymentFeeDTO.getFeeId(), foundFeeDTO.get().getFeeId());
        verify(paymentFeeRepository, times(1)).findById(paymentFeeDTO.getFeeId());
    }

    @Test
    void testUpdatePaymentFee() {
        paymentFeeDTO.setAmount(BigDecimal.valueOf(10.0));
        PaymentFee updatedPaymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);

        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(updatedPaymentFee);

        PaymentFeeDTO updatedFeeDTO = paymentFeeService.save(paymentFeeDTO);

        assertNotNull(updatedFeeDTO, "PaymentFee update failed, returned object is null.");
        assertEquals(BigDecimal.valueOf(10.0), updatedFeeDTO.getAmount(), "Fee amount did not update correctly.");
        verify(paymentFeeRepository, times(1)).save(any(PaymentFee.class));
    }


    @Test
    void testSoftDeletePaymentFee() {
        PaymentFee paymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);
        ArgumentCaptor<PaymentFee> captor = ArgumentCaptor.forClass(PaymentFee.class);

        when(paymentFeeRepository.findById(paymentFeeDTO.getFeeId())).thenReturn(Optional.of(paymentFee));
        when(paymentFeeRepository.save(any(PaymentFee.class))).thenReturn(paymentFee);

        paymentFeeService.softDelete(paymentFeeDTO.getFeeId(), "testUser");

        verify(paymentFeeRepository, times(1)).findById(paymentFeeDTO.getFeeId());
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
        PaymentFee paymentFee = modelMapper.map(paymentFeeDTO, PaymentFee.class);
        when(paymentFeeRepository.findAll()).thenReturn(List.of(paymentFee));

        List<PaymentFeeDTO> feesListDTO = paymentFeeService.findAll();

        assertNotNull(feesListDTO, "PaymentFee list is null.");
        assertFalse(feesListDTO.isEmpty(), "PaymentFee list is empty.");
        assertEquals(1, feesListDTO.size(), "PaymentFee list size mismatch.");
        verify(paymentFeeRepository, times(1)).findAll();
    }

    @Test
    void testFindAllPaymentFeesEmptyList() {
        when(paymentFeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<PaymentFeeDTO> feesListDTO = paymentFeeService.findAll();

        assertTrue(feesListDTO.isEmpty(), "findAll should return an empty list if no payment fees exist");
    }



}
