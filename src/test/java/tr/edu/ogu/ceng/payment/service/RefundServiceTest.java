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
import tr.edu.ogu.ceng.payment.dto.RefundDTO;
import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.repository.RefundRepository;

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
public class RefundServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private RefundRepository refundRepository;

    @Autowired
    private RefundService refundService;

    @Autowired
    private ModelMapper modelMapper;

    private RefundDTO refundDTO;

    @BeforeEach
    void setUp() {
        reset(refundRepository);

        refundDTO = new RefundDTO();
        refundDTO.setRefundId(1L);
        refundDTO.setRefundAmount(BigDecimal.valueOf(50.00));
        refundDTO.setRefundReason("Product returned");
        refundDTO.setStatus("Pending");
        refundDTO.setRefundDate(LocalDateTime.now());
        refundDTO.setRefundMethod("Bank Transfer");
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateRefund() {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        RefundDTO createdRefundDTO = refundService.save(refundDTO);

        assertNotNull(createdRefundDTO, "Refund creation failed, returned object is null.");
        assertEquals(refundDTO.getRefundId(), createdRefundDTO.getRefundId());
        verify(refundRepository, times(1)).save(any(Refund.class));
    }

    @Test
    void testFindRefundById_NotFound() {
        when(refundRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<RefundDTO> foundRefundDTO = refundService.findById(999L);

        assertFalse(foundRefundDTO.isPresent(), "Refund should not be found.");
        verify(refundRepository, times(1)).findById(999L);
    }

    @Test
    void testFindRefundById() {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        when(refundRepository.findById(refundDTO.getRefundId())).thenReturn(Optional.of(refund));

        Optional<RefundDTO> foundRefundDTO = refundService.findById(refundDTO.getRefundId());

        assertTrue(foundRefundDTO.isPresent(), "Refund not found.");
        assertEquals(refundDTO.getRefundId(), foundRefundDTO.get().getRefundId());
        verify(refundRepository, times(1)).findById(refundDTO.getRefundId());
    }

    @Test
    void testUpdateRefund() {
        refundDTO.setStatus("Approved");
        Refund updatedRefund = modelMapper.map(refundDTO, Refund.class);

        when(refundRepository.save(any(Refund.class))).thenReturn(updatedRefund);

        RefundDTO updatedRefundDTO = refundService.save(refundDTO);

        assertNotNull(updatedRefundDTO, "Refund update failed, returned object is null.");
        assertEquals("Approved", updatedRefundDTO.getStatus(), "Status did not update correctly.");
        verify(refundRepository, times(1)).save(any(Refund.class));
    }

    @Test
    void testSoftDeleteRefund() {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        ArgumentCaptor<Refund> captor = ArgumentCaptor.forClass(Refund.class);

        when(refundRepository.findById(refundDTO.getRefundId())).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        refundService.softDelete(refundDTO.getRefundId(), "testUser");

        verify(refundRepository, times(1)).findById(refundDTO.getRefundId());
        verify(refundRepository, times(1)).save(captor.capture());

        Refund softDeletedRefund = captor.getValue();
        assertNotNull(softDeletedRefund.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedRefund.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteRefund_NotFound() {
        when(refundRepository.findById(anyLong())).thenReturn(Optional.empty());

        refundService.softDelete(999L, "testUser");

        verify(refundRepository, times(1)).findById(999L);
        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void testFindAllRefunds() {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        when(refundRepository.findAll()).thenReturn(List.of(refund));

        List<RefundDTO> refundDTOList = refundService.findAll();

        assertNotNull(refundDTOList, "Refund list is null.");
        assertFalse(refundDTOList.isEmpty(), "Refund list is empty.");
        assertEquals(1, refundDTOList.size(), "Refund list size mismatch.");
        verify(refundRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsRefund() {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        refund.setStatus("COMPLETED");
        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

        Optional<RefundDTO> result = refundService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a refund when the ID is valid");
        assertEquals("COMPLETED", result.get().getStatus(), "Status should match");
    }

}
