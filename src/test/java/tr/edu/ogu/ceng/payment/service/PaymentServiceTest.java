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
import tr.edu.ogu.ceng.payment.dto.PaymentDTO;
import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.repository.PaymentRepository;

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
public class PaymentServiceTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ModelMapper modelMapper;

    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        reset(paymentRepository);

        paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(1L);
        paymentDTO.setUserId(UUID.randomUUID());
        paymentDTO.setAmount(BigDecimal.valueOf(150.75));
        paymentDTO.setStatus("Pending");
        paymentDTO.setTransactionDate(LocalDateTime.now());
        paymentDTO.setDescription("Test payment description");
    }

    @Test
    void testCreatePayment() {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDTO createdPaymentDTO = paymentService.save(paymentDTO);

        assertNotNull(createdPaymentDTO, "Payment creation failed, returned object is null.");
        assertEquals(paymentDTO.getPaymentId(), createdPaymentDTO.getPaymentId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testFindPaymentById_NotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentDTO> foundPaymentDTO = paymentService.findById(999L);

        assertFalse(foundPaymentDTO.isPresent(), "Payment should not be found.");
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentById() {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        when(paymentRepository.findById(paymentDTO.getPaymentId())).thenReturn(Optional.of(payment));

        Optional<PaymentDTO> foundPaymentDTO = paymentService.findById(paymentDTO.getPaymentId());

        assertTrue(foundPaymentDTO.isPresent(), "Payment not found.");
        assertEquals(paymentDTO.getPaymentId(), foundPaymentDTO.get().getPaymentId());
        verify(paymentRepository, times(1)).findById(paymentDTO.getPaymentId());
    }

    @Test
    void testUpdatePayment() {
        paymentDTO.setStatus("Completed");
        Payment updatedPayment = modelMapper.map(paymentDTO, Payment.class);

        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        PaymentDTO updatedPaymentDTO = paymentService.save(paymentDTO);

        assertNotNull(updatedPaymentDTO, "Payment update failed, returned object is null.");
        assertEquals("Completed", updatedPaymentDTO.getStatus(), "Status did not update correctly.");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testSoftDeletePayment() {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        when(paymentRepository.findById(paymentDTO.getPaymentId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.softDelete(paymentDTO.getPaymentId(), "testUser");

        verify(paymentRepository, times(1)).findById(paymentDTO.getPaymentId());
        verify(paymentRepository, times(1)).save(captor.capture());

        Payment softDeletedPayment = captor.getValue();
        assertNotNull(softDeletedPayment.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedPayment.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePayment_NotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentService.softDelete(999L, "testUser");

        verify(paymentRepository, times(1)).findById(999L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testFindAllPayments() {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentDTO> paymentsDTOList = paymentService.findAll();

        assertNotNull(paymentsDTOList, "Payment list is null.");
        assertFalse(paymentsDTOList.isEmpty(), "Payment list is empty.");
        assertEquals(1, paymentsDTOList.size(), "Payment list size mismatch.");
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsPayment() {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setAmount(BigDecimal.valueOf(200.0));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Optional<PaymentDTO> result = paymentService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a payment when the ID is valid");
        assertEquals(0, result.get().getAmount().compareTo(BigDecimal.valueOf(200.0)), "Payment amount should match");
    }


}
