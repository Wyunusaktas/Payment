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
import tr.edu.ogu.ceng.payment.dto.PaymentAttemptDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentAttempt;
import tr.edu.ogu.ceng.payment.repository.PaymentAttemptRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class PaymentAttemptServiceTest {

    @MockBean
    private PaymentAttemptRepository paymentAttemptRepository;

    @Autowired
    private PaymentAttemptService paymentAttemptService;

    @Autowired
    private ModelMapper modelMapper;

    private PaymentAttempt paymentAttempt;
    private PaymentAttemptDTO paymentAttemptDTO;

    @BeforeEach
    void setUp() {
        reset(paymentAttemptRepository);

        paymentAttempt = new PaymentAttempt();
        paymentAttempt.setAttemptId(1L);
        paymentAttempt.setUserId(UUID.randomUUID());
        paymentAttempt.setAmount(new BigDecimal("150.00"));
        paymentAttempt.setAttemptStatus("SUCCESS");
        paymentAttempt.setAttemptDate(LocalDateTime.now());
        paymentAttempt.setErrorMessage(null);

        paymentAttemptDTO = modelMapper.map(paymentAttempt, PaymentAttemptDTO.class);
    }

    @Test
    void testCreatePaymentAttempt() {
        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        PaymentAttemptDTO createdAttemptDTO = paymentAttemptService.save(paymentAttemptDTO);

        assertNotNull(createdAttemptDTO, "PaymentAttempt creation failed, returned object is null.");
        assertEquals(paymentAttemptDTO.getAttemptId(), createdAttemptDTO.getAttemptId());
        verify(paymentAttemptRepository, times(1)).save(any(PaymentAttempt.class));
    }

    @Test
    void testFindPaymentAttemptById_NotFound() {
        when(paymentAttemptRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentAttemptDTO> foundAttemptDTO = paymentAttemptService.findById(999L);

        assertFalse(foundAttemptDTO.isPresent(), "PaymentAttempt should not be found.");
        verify(paymentAttemptRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentAttemptById() {
        when(paymentAttemptRepository.findById(paymentAttempt.getAttemptId())).thenReturn(Optional.of(paymentAttempt));

        Optional<PaymentAttemptDTO> foundAttemptDTO = paymentAttemptService.findById(paymentAttemptDTO.getAttemptId());

        assertTrue(foundAttemptDTO.isPresent(), "PaymentAttempt not found.");
        assertEquals(paymentAttemptDTO.getAttemptId(), foundAttemptDTO.get().getAttemptId());
        verify(paymentAttemptRepository, times(1)).findById(paymentAttemptDTO.getAttemptId());
    }

    @Test
    void testUpdatePaymentAttempt() {
        paymentAttemptDTO.setAttemptStatus("FAILED");

        // paymentAttemptDTO'yu paymentAttempt'a mapleyin
        PaymentAttempt updatedPaymentAttempt = modelMapper.map(paymentAttemptDTO, PaymentAttempt.class);

        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(updatedPaymentAttempt);

        PaymentAttemptDTO updatedAttemptDTO = paymentAttemptService.save(paymentAttemptDTO);

        assertNotNull(updatedAttemptDTO, "PaymentAttempt update failed, returned object is null.");
        assertEquals("FAILED", updatedAttemptDTO.getAttemptStatus(), "Attempt status did not update correctly.");
        verify(paymentAttemptRepository, times(1)).save(any(PaymentAttempt.class));
    }


    @Test
    void testSoftDeletePaymentAttempt() {
        ArgumentCaptor<PaymentAttempt> captor = ArgumentCaptor.forClass(PaymentAttempt.class);

        when(paymentAttemptRepository.findById(paymentAttempt.getAttemptId())).thenReturn(Optional.of(paymentAttempt));
        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(paymentAttempt);

        paymentAttemptService.softDelete(paymentAttemptDTO.getAttemptId(), "testUser");

        verify(paymentAttemptRepository, times(1)).findById(paymentAttemptDTO.getAttemptId());
        verify(paymentAttemptRepository, times(1)).save(captor.capture());

        PaymentAttempt softDeletedAttempt = captor.getValue();
        assertNotNull(softDeletedAttempt.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedAttempt.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentAttempt_NotFound() {
        when(paymentAttemptRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentAttemptService.softDelete(999L, "testUser");

        verify(paymentAttemptRepository, times(1)).findById(999L);
        verify(paymentAttemptRepository, never()).save(any(PaymentAttempt.class));
    }

    @Test
    void testFindAllPaymentAttempts() {
        when(paymentAttemptRepository.findAll()).thenReturn(List.of(paymentAttempt));

        List<PaymentAttemptDTO> attemptsDTOList = paymentAttemptService.findAll();

        assertNotNull(attemptsDTOList, "PaymentAttempt list is null.");
        assertFalse(attemptsDTOList.isEmpty(), "PaymentAttempt list is empty.");
        assertEquals(1, attemptsDTOList.size(), "PaymentAttempt list size mismatch.");
        verify(paymentAttemptRepository, times(1)).findAll();
    }

    @Test
    void testSavePaymentAttemptWithValidAmount() {
        paymentAttemptDTO.setAmount(BigDecimal.valueOf(250.0));

        // DTO'yu model nesnesine dönüştürün
        PaymentAttempt updatedPaymentAttempt = modelMapper.map(paymentAttemptDTO, PaymentAttempt.class);

        when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenReturn(updatedPaymentAttempt);

        PaymentAttemptDTO result = paymentAttemptService.save(paymentAttemptDTO);

        assertNotNull(result, "Saved PaymentAttemptDTO should not be null");
        assertEquals(BigDecimal.valueOf(250.0), result.getAmount(), "Amount should match the provided value");
    }



}
