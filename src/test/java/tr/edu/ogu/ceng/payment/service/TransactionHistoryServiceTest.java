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
import tr.edu.ogu.ceng.payment.dto.TransactionHistoryDTO;
import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
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

    @MockBean
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private ModelMapper modelMapper;

    private TransactionHistoryDTO transactionHistoryDTO;

    @BeforeEach
    void setUp() {
        reset(transactionHistoryRepository);

        transactionHistoryDTO = new TransactionHistoryDTO();
        transactionHistoryDTO.setHistoryId(1L);
        transactionHistoryDTO.setUserId(UUID.randomUUID());
        transactionHistoryDTO.setTransactionType("PURCHASE");
        transactionHistoryDTO.setAmount(new BigDecimal("200.00"));
        transactionHistoryDTO.setTransactionDate(LocalDateTime.now());
        transactionHistoryDTO.setStatus("COMPLETED");
    }

    @Test
    void testCreateTransactionHistory() {
        TransactionHistory transactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        TransactionHistoryDTO createdTransactionHistoryDTO = transactionHistoryService.save(transactionHistoryDTO);

        assertNotNull(createdTransactionHistoryDTO, "TransactionHistory creation failed, returned object is null.");
        assertEquals(transactionHistoryDTO.getStatus(), createdTransactionHistoryDTO.getStatus());
        verify(transactionHistoryRepository, times(1)).save(any(TransactionHistory.class));
    }

    @Test
    void testFindTransactionHistoryById_NotFound() {
        when(transactionHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<TransactionHistoryDTO> foundTransactionHistoryDTO = transactionHistoryService.findById(999L);

        assertFalse(foundTransactionHistoryDTO.isPresent(), "TransactionHistory should not be found.");
        verify(transactionHistoryRepository, times(1)).findById(999L);
    }

    @Test
    void testFindTransactionHistoryById() {
        TransactionHistory transactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);
        when(transactionHistoryRepository.findById(transactionHistoryDTO.getHistoryId())).thenReturn(Optional.of(transactionHistory));

        Optional<TransactionHistoryDTO> foundTransactionHistoryDTO = transactionHistoryService.findById(transactionHistoryDTO.getHistoryId());

        assertTrue(foundTransactionHistoryDTO.isPresent(), "TransactionHistory not found.");
        assertEquals(transactionHistoryDTO.getStatus(), foundTransactionHistoryDTO.get().getStatus());
        verify(transactionHistoryRepository, times(1)).findById(transactionHistoryDTO.getHistoryId());
    }

    @Test
    void testUpdateTransactionHistory() {
        transactionHistoryDTO.setStatus("UPDATED");
        TransactionHistory updatedTransactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);

        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(updatedTransactionHistory);

        TransactionHistoryDTO updatedTransactionHistoryDTO = transactionHistoryService.save(transactionHistoryDTO);

        assertNotNull(updatedTransactionHistoryDTO, "TransactionHistory update failed, returned object is null.");
        assertEquals("UPDATED", updatedTransactionHistoryDTO.getStatus(), "TransactionHistory status did not update correctly.");
        verify(transactionHistoryRepository, times(1)).save(any(TransactionHistory.class));
    }

    @Test
    void testSoftDeleteTransactionHistory() {
        TransactionHistory transactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);
        ArgumentCaptor<TransactionHistory> captor = ArgumentCaptor.forClass(TransactionHistory.class);

        when(transactionHistoryRepository.findById(transactionHistoryDTO.getHistoryId())).thenReturn(Optional.of(transactionHistory));
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        transactionHistoryService.softDelete(transactionHistoryDTO.getHistoryId(), "testUser");

        verify(transactionHistoryRepository, times(1)).findById(transactionHistoryDTO.getHistoryId());
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
        TransactionHistory transactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);
        when(transactionHistoryRepository.findAll()).thenReturn(List.of(transactionHistory));

        List<TransactionHistoryDTO> transactionHistoryDTOList = transactionHistoryService.findAll();

        assertNotNull(transactionHistoryDTOList, "TransactionHistory list is null.");
        assertFalse(transactionHistoryDTOList.isEmpty(), "TransactionHistory list is empty.");
        assertEquals(1, transactionHistoryDTOList.size(), "TransactionHistory list size mismatch.");
        verify(transactionHistoryRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsTransactionHistory() {
        TransactionHistory history = new TransactionHistory();
        history.setStatus("SUCCESS");
        when(transactionHistoryRepository.findById(1L)).thenReturn(Optional.of(history));

        Optional<TransactionHistoryDTO> result = transactionHistoryService.findById(1L);

        assertTrue(result.isPresent(), "findById should return a transaction history when the ID is valid");
        assertEquals("SUCCESS", result.get().getStatus(), "Status should match");
    }


}
