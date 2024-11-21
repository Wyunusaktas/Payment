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
import tr.edu.ogu.ceng.payment.dto.TransactionDTO;
import tr.edu.ogu.ceng.payment.entity.Transaction;
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

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ModelMapper modelMapper;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        reset(transactionRepository);

        transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId(1L);
        transactionDTO.setOrderId(UUID.randomUUID());
        transactionDTO.setStatus("COMPLETED");
        transactionDTO.setTransactionDate(LocalDateTime.now());
        transactionDTO.setAmount(new BigDecimal("150.50"));
    }


    @Test
    void testCreateTransaction() {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO createdTransactionDTO = transactionService.save(transactionDTO);

        assertNotNull(createdTransactionDTO, "Transaction creation failed, returned object is null.");
        assertEquals(transactionDTO.getStatus(), createdTransactionDTO.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testFindTransactionById_NotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<TransactionDTO> foundTransactionDTO = transactionService.findById(999L);

        assertFalse(foundTransactionDTO.isPresent(), "Transaction should not be found.");
        verify(transactionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindTransactionById() {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        when(transactionRepository.findById(transactionDTO.getTransactionId())).thenReturn(Optional.of(transaction));

        Optional<TransactionDTO> foundTransactionDTO = transactionService.findById(transactionDTO.getTransactionId());

        assertTrue(foundTransactionDTO.isPresent(), "Transaction not found.");
        assertEquals(transactionDTO.getStatus(), foundTransactionDTO.get().getStatus());
        verify(transactionRepository, times(1)).findById(transactionDTO.getTransactionId());
    }

    @Test
    void testUpdateTransaction() {
        transactionDTO.setStatus("UPDATED");
        Transaction updatedTransaction = modelMapper.map(transactionDTO, Transaction.class);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        TransactionDTO updatedTransactionDTO = transactionService.save(transactionDTO);

        assertNotNull(updatedTransactionDTO, "Transaction update failed, returned object is null.");
        assertEquals("UPDATED", updatedTransactionDTO.getStatus(), "Transaction status did not update correctly.");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSoftDeleteTransaction() {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        when(transactionRepository.findById(transactionDTO.getTransactionId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.softDelete(transactionDTO.getTransactionId(), "testUser");

        verify(transactionRepository, times(1)).findById(transactionDTO.getTransactionId());
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
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionDTO> transactionDTOList = transactionService.findAll();

        assertNotNull(transactionDTOList, "Transaction list is null.");
        assertFalse(transactionDTOList.isEmpty(), "Transaction list is empty.");
        assertEquals(1, transactionDTOList.size(), "Transaction list size mismatch.");
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
