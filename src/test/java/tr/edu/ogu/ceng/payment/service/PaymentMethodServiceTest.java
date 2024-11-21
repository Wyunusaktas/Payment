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
import tr.edu.ogu.ceng.payment.dto.PaymentMethodDTO;
import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.repository.PaymentMethodRepository;

import java.time.LocalDate;
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
public class PaymentMethodServiceTest {

    @MockBean
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private ModelMapper modelMapper;

    private PaymentMethodDTO paymentMethodDTO;

    @BeforeEach
    void setUp() {
        reset(paymentMethodRepository);

        paymentMethodDTO = new PaymentMethodDTO();
        paymentMethodDTO.setMethodId(1L);
        paymentMethodDTO.setUserId(UUID.randomUUID());
        paymentMethodDTO.setType("Credit Card");
        paymentMethodDTO.setProvider("Visa");
        paymentMethodDTO.setAccountNumber("123456789");
        paymentMethodDTO.setExpiryDate(LocalDate.now().plusYears(2));
        paymentMethodDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreatePaymentMethod() {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethodDTO createdMethodDTO = paymentMethodService.save(paymentMethodDTO);

        assertNotNull(createdMethodDTO, "PaymentMethod creation failed, returned object is null.");
        assertEquals(paymentMethodDTO.getMethodId(), createdMethodDTO.getMethodId());
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void testFindPaymentMethodById_NotFound() {
        when(paymentMethodRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PaymentMethodDTO> foundMethodDTO = paymentMethodService.findById(999L);

        assertFalse(foundMethodDTO.isPresent(), "PaymentMethod should not be found.");
        verify(paymentMethodRepository, times(1)).findById(999L);
    }

    @Test
    void testFindPaymentMethodById() {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        when(paymentMethodRepository.findById(paymentMethodDTO.getMethodId())).thenReturn(Optional.of(paymentMethod));

        Optional<PaymentMethodDTO> foundMethodDTO = paymentMethodService.findById(paymentMethodDTO.getMethodId());

        assertTrue(foundMethodDTO.isPresent(), "PaymentMethod not found.");
        assertEquals(paymentMethodDTO.getMethodId(), foundMethodDTO.get().getMethodId());
        verify(paymentMethodRepository, times(1)).findById(paymentMethodDTO.getMethodId());
    }

    @Test
    void testUpdatePaymentMethod() {
        paymentMethodDTO.setProvider("MasterCard");
        PaymentMethod updatedPaymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);

        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(updatedPaymentMethod);

        PaymentMethodDTO updatedMethodDTO = paymentMethodService.save(paymentMethodDTO);

        assertNotNull(updatedMethodDTO, "PaymentMethod update failed, returned object is null.");
        assertEquals("MasterCard", updatedMethodDTO.getProvider(), "Provider did not update correctly.");
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void testSoftDeletePaymentMethod() {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);

        when(paymentMethodRepository.findById(paymentMethodDTO.getMethodId())).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        paymentMethodService.softDelete(paymentMethodDTO.getMethodId(), "testUser");

        verify(paymentMethodRepository, times(1)).findById(paymentMethodDTO.getMethodId());
        verify(paymentMethodRepository, times(1)).save(captor.capture());

        PaymentMethod softDeletedMethod = captor.getValue();
        assertNotNull(softDeletedMethod.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedMethod.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeletePaymentMethod_NotFound() {
        when(paymentMethodRepository.findById(anyLong())).thenReturn(Optional.empty());

        paymentMethodService.softDelete(999L, "testUser");

        verify(paymentMethodRepository, times(1)).findById(999L);
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testFindAllPaymentMethods() {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        when(paymentMethodRepository.findAll()).thenReturn(List.of(paymentMethod));

        List<PaymentMethodDTO> methodsListDTO = paymentMethodService.findAll();

        assertNotNull(methodsListDTO, "PaymentMethod list is null.");
        assertFalse(methodsListDTO.isEmpty(), "PaymentMethod list is empty.");
        assertEquals(1, methodsListDTO.size(), "PaymentMethod list size mismatch.");
        verify(paymentMethodRepository, times(1)).findAll();
    }

    @Test
    void testSavePaymentMethodNotNull() {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        paymentMethod.setProvider("Visa");

        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);

        PaymentMethodDTO result = paymentMethodService.save(paymentMethodDTO);

        assertNotNull(result, "Saved payment method should not be null");
        assertEquals("Visa", result.getProvider(), "Provider should match");
    }


}
