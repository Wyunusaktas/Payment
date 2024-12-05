package tr.edu.ogu.ceng.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.service.RefundService;

public class RefundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundController refundController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(refundController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule
}

 
   
    // Test GET /api/refunds/status/{status}
    @Test
    void testGetRefundsByStatus() throws Exception {
        String status = "PENDING";
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = payment; // Simulate saving payment

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Link the Refund to the Payment
        refund.setRefundAmount(BigDecimal.valueOf(50.75));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus(status);

        // Mock the service call
        when(refundService.getRefundsByStatus(status)).thenReturn(Arrays.asList(refund));

        mockMvc.perform(get("/api/refunds/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value(status));
    }

    // Test GET /api/refunds/date-range
    @Test
    void testGetRefundsByDateRange() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = payment; // Simulate saving payment

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Link the Refund to the Payment
        refund.setRefundAmount(BigDecimal.valueOf(50.75));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("PENDING");

        // Mock the service behavior
        when(refundService.getRefundsByDateRange(startDate, endDate)).thenReturn(Arrays.asList(refund));

        mockMvc.perform(get("/api/refunds/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    // Test GET /api/refunds/total-amount
    @Test
    void testCalculateTotalRefundAmount() throws Exception {
        BigDecimal totalAmount = BigDecimal.valueOf(500);
        when(refundService.calculateTotalRefundAmount()).thenReturn(totalAmount);

        mockMvc.perform(get("/api/refunds/total-amount"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(500));
    }

    // Test POST /api/refunds
    @Test
    void testAddRefund() throws Exception {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = payment; // Simulate saving the payment

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Link the Refund to the Payment
        refund.setRefundAmount(BigDecimal.valueOf(50.75));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("PENDING");

        // Mock the service call
        when(refundService.addRefund(any(Refund.class))).thenReturn(refund);

        mockMvc.perform(post("/api/refunds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refund)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.refundAmount").value(50.75))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // Test PUT /api/refunds
    @Test
    void testUpdateRefund() throws Exception {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("COMPLETED");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        payment.setRecurring(false);
        payment.setPaymentChannel("Online");

        Payment savedPayment = payment; // Simulate saving the payment

        Refund refund = new Refund();
        refund.setRefundId(UUID.randomUUID());
        refund.setPayment(savedPayment);  // Link the Refund to the Payment
        refund.setRefundAmount(BigDecimal.valueOf(50.75));
        refund.setRefundDate(LocalDateTime.now());
        refund.setStatus("PENDING");

        when(refundService.updateRefund(any(Refund.class))).thenReturn(refund);

        mockMvc.perform(put("/api/refunds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refund)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.refundAmount").value(50.75))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // Test DELETE /api/refunds/{refundId}
    @Test
    void testDeleteRefund() throws Exception {
        UUID refundId = UUID.randomUUID();
        doNothing().when(refundService).deleteRefund(refundId);

        mockMvc.perform(delete("/api/refunds/{refundId}", refundId))
                .andExpect(status().isNoContent());
    }
}
