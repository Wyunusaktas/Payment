package tr.edu.ogu.ceng.payment.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
import tr.edu.ogu.ceng.payment.service.TransactionHistoryService;

public class TransactionHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionHistoryService transactionHistoryService;

    @InjectMocks
    private TransactionHistoryController transactionHistoryController;

    private ObjectMapper objectMapper;

      @BeforeEach
    void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(transactionHistoryController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule
}

    @Test
    void testGetTransactionHistoryByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        TransactionHistory history = new TransactionHistory();
        history.setUserId(userId);
        history.setTransactionType("WITHDRAWAL");
        history.setAmount(BigDecimal.valueOf(50.00));

        when(transactionHistoryService.getTransactionHistoryByUserId(userId)).thenReturn(Arrays.asList(history));

        mockMvc.perform(get("/api/transaction-history/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[0].amount").value(50.00));
    }

   
    @Test
    void testAddTransactionHistory() throws Exception {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionType("DEPOSIT");
        transactionHistory.setAmount(BigDecimal.valueOf(1000.00));

        when(transactionHistoryService.addTransactionHistory(any(TransactionHistory.class)))
                .thenReturn(transactionHistory);

        mockMvc.perform(post("/api/transaction-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionHistory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.00));
    }

    @Test
    void testUpdateTransactionHistory() throws Exception {
        UUID transactionHistoryId = UUID.randomUUID();
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionType("DEPOSIT");
        transactionHistory.setAmount(BigDecimal.valueOf(1200.00));

        when(transactionHistoryService.updateTransactionHistory(any(TransactionHistory.class)))
                .thenReturn(transactionHistory);

        mockMvc.perform(put("/api/transaction-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionHistory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1200.00));
    }

    @Test
    void testDeleteTransactionHistory() throws Exception {
        UUID transactionHistoryId = UUID.randomUUID();

        doNothing().when(transactionHistoryService).deleteTransactionHistory(transactionHistoryId);

        mockMvc.perform(delete("/api/transaction-history/{transactionHistoryId}", transactionHistoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(transactionHistoryService, times(1)).deleteTransactionHistory(transactionHistoryId);
    }
}
