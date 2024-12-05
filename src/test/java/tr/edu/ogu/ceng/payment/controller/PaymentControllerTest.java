package tr.edu.ogu.ceng.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.service.PaymentService;

public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register the JavaTimeModule
    }

    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus("PENDING");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setDescription("Test Payment");
        return payment;
    }

    @Test
    public void testGetAllPaymentsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        Payment payment = createTestPayment();
        payment.setUserId(userId);

        when(paymentService.getAllPaymentsByUserId(userId)).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    public void testGetPaymentsByStatus() throws Exception {
        Payment payment = createTestPayment();
        payment.setStatus("COMPLETED");

        when(paymentService.getPaymentsByStatus("COMPLETED")).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments/status/{status}", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    public void testAddPayment() throws Exception {
        Payment payment = createTestPayment();

        when(paymentService.addPayment(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testDeletePayment() throws Exception {
        UUID paymentId = UUID.randomUUID();
        doNothing().when(paymentService).deletePayment(paymentId);

        mockMvc.perform(delete("/api/payments/{paymentId}", paymentId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCalculateTotalAmount() throws Exception {
        BigDecimal totalAmount = BigDecimal.valueOf(500.00);
        when(paymentService.calculateTotalAmount()).thenReturn(totalAmount);

        mockMvc.perform(get("/api/payments/total-amount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500.00));
    }

    @Test
    public void testUpdatePayment() throws Exception {
        Payment payment = createTestPayment();

        when(paymentService.updatePayment(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(put("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testUpdatePayment_NotFound() throws Exception {
        Payment payment = createTestPayment();

        when(paymentService.updatePayment(any(Payment.class))).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(put("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPaymentsByDateRange_InvalidDateRange() throws Exception {    
        String invalidStartDate = "2024-13-32T00:00:00";  // Invalid date
        String invalidEndDate = "2024-13-32T23:59:59";  // Invalid date

        mockMvc.perform(get("/api/payments/date-range")
            .param("startDate", invalidStartDate)
            .param("endDate", invalidEndDate))
            .andExpect(status().isBadRequest());
    }
    @Test
    public void testGetPaymentsByUserId_UserNotFound() throws Exception {
    UUID userId = UUID.randomUUID();  // UserID that doesn't exist
    when(paymentService.getAllPaymentsByUserId(userId)).thenReturn(List.of());

    mockMvc.perform(get("/api/payments/user/{userId}", userId))
            .andExpect(status().isNoContent());
}
@Test
public void testCalculateTotalAmountByStatus_NoPaymentsForStatus() throws Exception {
    when(paymentService.calculateTotalAmountByStatus("COMPLETED")).thenReturn(BigDecimal.ZERO);

    mockMvc.perform(get("/api/payments/total-amount/status/{status}", "COMPLETED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(0.00));
}
@Test
public void testCalculateTotalAmount_EmptyPayments() throws Exception {
    when(paymentService.calculateTotalAmount()).thenReturn(BigDecimal.ZERO);

    mockMvc.perform(get("/api/payments/total-amount"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(0.00));
}



}
