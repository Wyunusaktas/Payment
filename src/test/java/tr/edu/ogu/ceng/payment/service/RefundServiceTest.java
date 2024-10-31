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
import tr.edu.ogu.ceng.payment.model.Refund;
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

    private Refund refund;

    @BeforeEach
    void setUp() {
        reset(refundRepository);

        refund = new Refund();
        refund.setRefundId(1L);
        refund.setRefundAmount(BigDecimal.valueOf(50.00));
        refund.setRefundReason("Product returned");
        refund.setStatus("Pending");
        refund.setRefundDate(LocalDateTime.now());
        refund.setRefundMethod("Bank Transfer");
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateRefund() {
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund createdRefund = refundService.save(refund);

        assertNotNull(createdRefund, "Refund creation failed, returned object is null.");
        assertEquals(refund.getRefundId(), createdRefund.getRefundId());
        verify(refundRepository, times(1)).save(any(Refund.class));
    }

    @Test
    void testFindRefundById_NotFound() {
        when(refundRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Refund> foundRefund = refundService.findById(999L);

        assertFalse(foundRefund.isPresent(), "Refund should not be found.");
        verify(refundRepository, times(1)).findById(999L);
    }

    @Test
    void testFindRefundById() {
        when(refundRepository.findById(refund.getRefundId())).thenReturn(Optional.of(refund));

        Optional<Refund> foundRefund = refundService.findById(refund.getRefundId());

        assertTrue(foundRefund.isPresent(), "Refund not found.");
        assertEquals(refund.getRefundId(), foundRefund.get().getRefundId());
        verify(refundRepository, times(1)).findById(refund.getRefundId());
    }

    @Test
    void testUpdateRefund() {
        refund.setStatus("Approved");

        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund updatedRefund = refundService.save(refund);

        assertNotNull(updatedRefund, "Refund update failed, returned object is null.");
        assertEquals("Approved", updatedRefund.getStatus(), "Status did not update correctly.");
        verify(refundRepository, times(1)).save(refund);
    }

    @Test
    void testSoftDeleteRefund() {
        ArgumentCaptor<Refund> captor = ArgumentCaptor.forClass(Refund.class);

        when(refundRepository.findById(refund.getRefundId())).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        refundService.softDelete(refund.getRefundId(), "testUser");

        verify(refundRepository, times(1)).findById(refund.getRefundId());
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
        when(refundRepository.findAll()).thenReturn(List.of(refund));

        List<Refund> refundList = refundService.findAll();

        assertNotNull(refundList, "Refund list is null.");
        assertFalse(refundList.isEmpty(), "Refund list is empty.");
        assertEquals(1, refundList.size(), "Refund list size mismatch.");
        verify(refundRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsRefund() {
        Refund refund = new Refund();
        refund.setStatus("COMPLETED");
        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

        Optional<Refund> result = refundService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a refund when the ID is valid");
        assertEquals("COMPLETED", result.get().getStatus(), "Status should match");
    }

}
