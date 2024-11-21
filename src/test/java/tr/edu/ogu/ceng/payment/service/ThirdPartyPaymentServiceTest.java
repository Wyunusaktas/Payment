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
import tr.edu.ogu.ceng.payment.dto.ThirdPartyPaymentDTO;
import tr.edu.ogu.ceng.payment.entity.ThirdPartyPayment;
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

    @MockBean
    private ThirdPartyPaymentRepository thirdPartyPaymentRepository;

    @Autowired
    private ThirdPartyPaymentService thirdPartyPaymentService;

    @Autowired
    private ModelMapper modelMapper;

    private ThirdPartyPaymentDTO thirdPartyPaymentDTO;

    @BeforeEach
    void setUp() {
        reset(thirdPartyPaymentRepository);

        thirdPartyPaymentDTO = new ThirdPartyPaymentDTO();
        thirdPartyPaymentDTO.setThirdPartyPaymentId(1L);
        thirdPartyPaymentDTO.setProvider("Provider A");
        thirdPartyPaymentDTO.setTransactionReference("TXN12345");
        thirdPartyPaymentDTO.setStatus("COMPLETED");
        thirdPartyPaymentDTO.setProcessedAt(LocalDateTime.now());
    }


    @Test
    void testCreateThirdPartyPayment() {
        ThirdPartyPayment thirdPartyPayment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(thirdPartyPayment);

        ThirdPartyPaymentDTO createdThirdPartyPaymentDTO = thirdPartyPaymentService.save(thirdPartyPaymentDTO);

        assertNotNull(createdThirdPartyPaymentDTO, "ThirdPartyPayment creation failed, returned object is null.");
        assertEquals(thirdPartyPaymentDTO.getProvider(), createdThirdPartyPaymentDTO.getProvider());
        verify(thirdPartyPaymentRepository, times(1)).save(any(ThirdPartyPayment.class));
    }

    @Test
    void testFindThirdPartyPaymentById_NotFound() {
        when(thirdPartyPaymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ThirdPartyPaymentDTO> foundThirdPartyPaymentDTO = thirdPartyPaymentService.findById(999L);

        assertFalse(foundThirdPartyPaymentDTO.isPresent(), "ThirdPartyPayment should not be found.");
        verify(thirdPartyPaymentRepository, times(1)).findById(999L);
    }

    @Test
    void testFindThirdPartyPaymentById() {
        ThirdPartyPayment thirdPartyPayment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);
        when(thirdPartyPaymentRepository.findById(thirdPartyPaymentDTO.getThirdPartyPaymentId())).thenReturn(Optional.of(thirdPartyPayment));

        Optional<ThirdPartyPaymentDTO> foundThirdPartyPaymentDTO = thirdPartyPaymentService.findById(thirdPartyPaymentDTO.getThirdPartyPaymentId());

        assertTrue(foundThirdPartyPaymentDTO.isPresent(), "ThirdPartyPayment not found.");
        assertEquals(thirdPartyPaymentDTO.getProvider(), foundThirdPartyPaymentDTO.get().getProvider());
        verify(thirdPartyPaymentRepository, times(1)).findById(thirdPartyPaymentDTO.getThirdPartyPaymentId());
    }

    @Test
    void testUpdateThirdPartyPayment() {
        thirdPartyPaymentDTO.setStatus("UPDATED");
        ThirdPartyPayment updatedThirdPartyPayment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);

        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(updatedThirdPartyPayment);

        ThirdPartyPaymentDTO updatedThirdPartyPaymentDTO = thirdPartyPaymentService.save(thirdPartyPaymentDTO);

        assertNotNull(updatedThirdPartyPaymentDTO, "ThirdPartyPayment update failed, returned object is null.");
        assertEquals("UPDATED", updatedThirdPartyPaymentDTO.getStatus(), "ThirdPartyPayment status did not update correctly.");
        verify(thirdPartyPaymentRepository, times(1)).save(any(ThirdPartyPayment.class));
    }

    @Test
    void testSoftDeleteThirdPartyPayment() {
        ThirdPartyPayment thirdPartyPayment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);
        ArgumentCaptor<ThirdPartyPayment> captor = ArgumentCaptor.forClass(ThirdPartyPayment.class);

        when(thirdPartyPaymentRepository.findById(thirdPartyPaymentDTO.getThirdPartyPaymentId())).thenReturn(Optional.of(thirdPartyPayment));
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(thirdPartyPayment);

        thirdPartyPaymentService.softDelete(thirdPartyPaymentDTO.getThirdPartyPaymentId(), "testUser");

        verify(thirdPartyPaymentRepository, times(1)).findById(thirdPartyPaymentDTO.getThirdPartyPaymentId());
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
        ThirdPartyPayment thirdPartyPayment = modelMapper.map(thirdPartyPaymentDTO, ThirdPartyPayment.class);
        when(thirdPartyPaymentRepository.findAll()).thenReturn(List.of(thirdPartyPayment));

        List<ThirdPartyPaymentDTO> thirdPartyPaymentDTOList = thirdPartyPaymentService.findAll();

        assertNotNull(thirdPartyPaymentDTOList, "ThirdPartyPayment list is null.");
        assertFalse(thirdPartyPaymentDTOList.isEmpty(), "ThirdPartyPayment list is empty.");
        assertEquals(1, thirdPartyPaymentDTOList.size(), "ThirdPartyPayment list size mismatch.");
        verify(thirdPartyPaymentRepository, times(1)).findAll();
    }

    @Test
    void testSaveThirdPartyPayment() {
        ThirdPartyPayment payment = new ThirdPartyPayment();
        payment.setProvider("PayPal");
        when(thirdPartyPaymentRepository.save(any(ThirdPartyPayment.class))).thenReturn(payment);

        ThirdPartyPaymentDTO paymentDTO = modelMapper.map(payment, ThirdPartyPaymentDTO.class);
        ThirdPartyPaymentDTO result = thirdPartyPaymentService.save(paymentDTO);

        assertNotNull(result, "Saved third-party payment should not be null");
        assertEquals("PayPal", result.getProvider(), "Provider should match");
    }



}
