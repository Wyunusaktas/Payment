package tr.edu.ogu.ceng.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.entity.Transaction;
import tr.edu.ogu.ceng.payment.service.TransactionService;

public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule
    }

    @Test
    void testGetTransactionsByPaymentId() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");
    
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setPayment(payment);
        transaction.setAmount(BigDecimal.valueOf(50.00));
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());
    
        when(transactionService.getTransactionsByPaymentId(paymentId))
                .thenReturn(Arrays.asList(transaction));
    
        mockMvc.perform(get("/api/transactions/payment/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Correct JSON path to access paymentId
                .andExpect(jsonPath("$[0].payment.paymentId").value(paymentId.toString()))  // Access paymentId inside the 'payment' object
                .andExpect(jsonPath("$[0].amount").value(50.00))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void testGetTransactionsByStatus() throws Exception {
        String status = "COMPLETED";
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setPayment(payment);
        transaction.setAmount(BigDecimal.valueOf(50.00));
        transaction.setStatus(status);
        transaction.setTransactionDate(LocalDateTime.now());

        when(transactionService.getTransactionsByStatus(status))
                .thenReturn(Arrays.asList(transaction));

        mockMvc.perform(get("/api/transactions/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value(status))
                .andExpect(jsonPath("$[0].amount").value(50.00));
    }

    @Test
    void testGetTransactionsByDateRange() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setPayment(payment);
        transaction.setAmount(BigDecimal.valueOf(50.00));
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());

        when(transactionService.getTransactionsByDateRange(startDate, endDate))
                .thenReturn(Arrays.asList(transaction));

        mockMvc.perform(get("/api/transactions/date-range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[0].amount").value(50.00));
    }

    @Test
    void testCalculateTotalTransactionAmount() throws Exception {
        BigDecimal totalAmount = BigDecimal.valueOf(100.00);
        when(transactionService.calculateTotalTransactionAmount()).thenReturn(totalAmount);
    
        // Corrected JSON path since the response is a direct numeric value
        mockMvc.perform(get("/api/transactions/total"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalAmount.toString()));  // Directly check the numeric value
    }

    @Test
    void testAddTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setPayment(new Payment());
        transaction.setAmount(BigDecimal.valueOf(200.00));
        transaction.setStatus("PENDING");
        transaction.setTransactionDate(LocalDateTime.now());

        when(transactionService.addTransaction(transaction)).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void testUpdateTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setPayment(new Payment());
        transaction.setAmount(BigDecimal.valueOf(150.00));
        transaction.setStatus("PENDING");
        transaction.setTransactionDate(LocalDateTime.now());

        when(transactionService.updateTransaction(transaction)).thenReturn(transaction);

        mockMvc.perform(put("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/api/transactions/{transactionId}", transactionId))
                .andExpect(status().isNoContent());
    }
}
