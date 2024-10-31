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
import tr.edu.ogu.ceng.payment.model.Transaction;
import tr.edu.ogu.ceng.payment.repository.TransactionRepository;

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
public class TransactionServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        reset(transactionRepository);

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setOrderId(UUID.randomUUID());
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(new BigDecimal("150.50"));
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction createdTransaction = transactionService.save(transaction);

        assertNotNull(createdTransaction, "Transaction creation failed, returned object is null.");
        assertEquals(transaction.getStatus(), createdTransaction.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testFindTransactionById_NotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Transaction> foundTransaction = transactionService.findById(999L);

        assertFalse(foundTransaction.isPresent(), "Transaction should not be found.");
        verify(transactionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindTransactionById() {
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));

        Optional<Transaction> foundTransaction = transactionService.findById(transaction.getTransactionId());

        assertTrue(foundTransaction.isPresent(), "Transaction not found.");
        assertEquals(transaction.getStatus(), foundTransaction.get().getStatus());
        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());
    }

    @Test
    void testUpdateTransaction() {
        transaction.setStatus("UPDATED");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction updatedTransaction = transactionService.save(transaction);

        assertNotNull(updatedTransaction, "Transaction update failed, returned object is null.");
        assertEquals("UPDATED", updatedTransaction.getStatus(), "Transaction status did not update correctly.");
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testSoftDeleteTransaction() {
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.softDelete(transaction.getTransactionId(), "testUser");

        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction softDeletedTransaction = captor.getValue();
        assertNotNull(softDeletedTransaction.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedTransaction.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteTransaction_NotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        transactionService.softDelete(999L, "testUser");

        verify(transactionRepository, times(1)).findById(999L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testFindAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<Transaction> transactionList = transactionService.findAll();

        assertNotNull(transactionList, "Transaction list is null.");
        assertFalse(transactionList.isEmpty(), "Transaction list is empty.");
        assertEquals(1, transactionList.size(), "Transaction list size mismatch.");
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testSoftDeleteUpdatesDeletedAtInTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.softDelete(transactionId, "testUser");

        assertNotNull(transaction.getDeletedAt(), "Deleted transaction should have a non-null deletedAt field");
        assertEquals("testUser", transaction.getDeletedBy(), "DeletedBy should match the provided user");
    }

}
