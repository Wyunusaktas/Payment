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
import tr.edu.ogu.ceng.payment.model.TransactionHistory;
import tr.edu.ogu.ceng.payment.repository.TransactionHistoryRepository;

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
public class TransactionHistoryServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    private TransactionHistory transactionHistory;

    @BeforeEach
    void setUp() {
        reset(transactionHistoryRepository);

        transactionHistory = new TransactionHistory();
        transactionHistory.setHistoryId(1L);
        transactionHistory.setUserId(UUID.randomUUID());
        transactionHistory.setTransactionType("PURCHASE");
        transactionHistory.setAmount(new BigDecimal("200.00"));
        transactionHistory.setTransactionDate(LocalDateTime.now());
        transactionHistory.setStatus("COMPLETED");
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateTransactionHistory() {
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        TransactionHistory createdTransactionHistory = transactionHistoryService.save(transactionHistory);

        assertNotNull(createdTransactionHistory, "TransactionHistory creation failed, returned object is null.");
        assertEquals(transactionHistory.getStatus(), createdTransactionHistory.getStatus());
        verify(transactionHistoryRepository, times(1)).save(any(TransactionHistory.class));
    }

    @Test
    void testFindTransactionHistoryById_NotFound() {
        when(transactionHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<TransactionHistory> foundTransactionHistory = transactionHistoryService.findById(999L);

        assertFalse(foundTransactionHistory.isPresent(), "TransactionHistory should not be found.");
        verify(transactionHistoryRepository, times(1)).findById(999L);
    }

    @Test
    void testFindTransactionHistoryById() {
        when(transactionHistoryRepository.findById(transactionHistory.getHistoryId())).thenReturn(Optional.of(transactionHistory));

        Optional<TransactionHistory> foundTransactionHistory = transactionHistoryService.findById(transactionHistory.getHistoryId());

        assertTrue(foundTransactionHistory.isPresent(), "TransactionHistory not found.");
        assertEquals(transactionHistory.getStatus(), foundTransactionHistory.get().getStatus());
        verify(transactionHistoryRepository, times(1)).findById(transactionHistory.getHistoryId());
    }

    @Test
    void testUpdateTransactionHistory() {
        transactionHistory.setStatus("UPDATED");

        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        TransactionHistory updatedTransactionHistory = transactionHistoryService.save(transactionHistory);

        assertNotNull(updatedTransactionHistory, "TransactionHistory update failed, returned object is null.");
        assertEquals("UPDATED", updatedTransactionHistory.getStatus(), "TransactionHistory status did not update correctly.");
        verify(transactionHistoryRepository, times(1)).save(transactionHistory);
    }

    @Test
    void testSoftDeleteTransactionHistory() {
        ArgumentCaptor<TransactionHistory> captor = ArgumentCaptor.forClass(TransactionHistory.class);

        when(transactionHistoryRepository.findById(transactionHistory.getHistoryId())).thenReturn(Optional.of(transactionHistory));
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        transactionHistoryService.softDelete(transactionHistory.getHistoryId(), "testUser");

        verify(transactionHistoryRepository, times(1)).findById(transactionHistory.getHistoryId());
        verify(transactionHistoryRepository, times(1)).save(captor.capture());

        TransactionHistory softDeletedTransactionHistory = captor.getValue();
        assertNotNull(softDeletedTransactionHistory.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedTransactionHistory.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteTransactionHistory_NotFound() {
        when(transactionHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        transactionHistoryService.softDelete(999L, "testUser");

        verify(transactionHistoryRepository, times(1)).findById(999L);
        verify(transactionHistoryRepository, never()).save(any(TransactionHistory.class));
    }

    @Test
    void testFindAllTransactionHistories() {
        when(transactionHistoryRepository.findAll()).thenReturn(List.of(transactionHistory));

        List<TransactionHistory> transactionHistoryList = transactionHistoryService.findAll();

        assertNotNull(transactionHistoryList, "TransactionHistory list is null.");
        assertFalse(transactionHistoryList.isEmpty(), "TransactionHistory list is empty.");
        assertEquals(1, transactionHistoryList.size(), "TransactionHistory list size mismatch.");
        verify(transactionHistoryRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsTransactionHistory() {
        TransactionHistory history = new TransactionHistory();
        history.setStatus("SUCCESS");
        when(transactionHistoryRepository.findById(1L)).thenReturn(Optional.of(history));

        Optional<TransactionHistory> result = transactionHistoryService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a transaction history when the ID is valid");
        assertEquals("SUCCESS", result.get().getStatus(), "Status should match");
    }

}
