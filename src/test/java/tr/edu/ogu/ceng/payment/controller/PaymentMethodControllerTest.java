package tr.edu.ogu.ceng.payment.controller;

import java.util.List;
import java.util.Optional;
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

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.service.PaymentMethodService;

public class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentMethodController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllPaymentMethodsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        PaymentMethod paymentMethod1 = new PaymentMethod();
        paymentMethod1.setUserId(userId);
        paymentMethod1.setType("Credit");
        paymentMethod1.setProvider("Visa");

        PaymentMethod paymentMethod2 = new PaymentMethod();
        paymentMethod2.setUserId(userId);
        paymentMethod2.setType("Digital");
        paymentMethod2.setProvider("PayPal");

        List<PaymentMethod> paymentMethods = List.of(paymentMethod1, paymentMethod2);

        when(paymentMethodService.getAllPaymentMethodsByUserId(userId)).thenReturn(paymentMethods);

        mockMvc.perform(get("/api/payment-methods/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetDefaultPaymentMethod() throws Exception {
        UUID userId = UUID.randomUUID();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUserId(userId);
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setDefault(true);

        when(paymentMethodService.getDefaultPaymentMethod(userId)).thenReturn(paymentMethod);

        mockMvc.perform(get("/api/payment-methods/user/{userId}/default", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.default").value(true));
    }

    @Test
    public void testGetPaymentMethodsByType() throws Exception {
        String type = "Credit";
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(type);
        paymentMethod.setProvider("Visa");

        List<PaymentMethod> paymentMethods = List.of(paymentMethod);

        when(paymentMethodService.getPaymentMethodsByType(type)).thenReturn(paymentMethods);

        mockMvc.perform(get("/api/payment-methods/type/{type}", type))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value(type));
    }

    @Test
    public void testGetPaymentMethodsByProvider() throws Exception {
        String provider = "Visa";
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setProvider(provider);
        paymentMethod.setType("Credit");

        List<PaymentMethod> paymentMethods = List.of(paymentMethod);

        when(paymentMethodService.getPaymentMethodsByProvider(provider)).thenReturn(paymentMethods);

        mockMvc.perform(get("/api/payment-methods/provider/{provider}", provider))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].provider").value(provider));
    }

    @Test
    public void testAddPaymentMethod() throws Exception {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("1234567890");

        when(paymentMethodService.addPaymentMethod(any(PaymentMethod.class))).thenReturn(paymentMethod);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMethod)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("Credit"))
                .andExpect(jsonPath("$.provider").value("Visa"));
    }

    @Test
    public void testUpdatePaymentMethod() throws Exception {
        UUID methodId = UUID.randomUUID();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(methodId);
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");
        paymentMethod.setAccountNumber("1234567890");

        when(paymentMethodService.updatePaymentMethod(any(PaymentMethod.class))).thenReturn(paymentMethod);

        mockMvc.perform(put("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentMethod)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("Credit"))
                .andExpect(jsonPath("$.provider").value("Visa"));
    }

    @Test
    public void testDeletePaymentMethod() throws Exception {
        UUID methodId = UUID.randomUUID();
        doNothing().when(paymentMethodService).deletePaymentMethod(methodId);

        mockMvc.perform(delete("/api/payment-methods/{id}", methodId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetPaymentMethodById() throws Exception {
        UUID methodId = UUID.randomUUID();
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodId(methodId);
        paymentMethod.setType("Credit");
        paymentMethod.setProvider("Visa");

        when(paymentMethodService.getPaymentMethodById(methodId)).thenReturn(Optional.of(paymentMethod));

        mockMvc.perform(get("/api/payment-methods/{id}", methodId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("Credit"))
                .andExpect(jsonPath("$.provider").value("Visa"));
    }

    @Test
    public void testExistsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        when(paymentMethodService.existsByUserId(userId)).thenReturn(true);

        mockMvc.perform(get("/api/payment-methods/exists/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
    }
}
