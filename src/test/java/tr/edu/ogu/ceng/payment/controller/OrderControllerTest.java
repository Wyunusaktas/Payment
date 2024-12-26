package tr.edu.ogu.ceng.payment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tr.edu.ogu.ceng.payment.restClientOrder.Order;
import tr.edu.ogu.ceng.payment.service.OrderService;

@SpringBootTest
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testCompleteOrder() throws Exception {
        // Arrange
        String userId = "testuser";
        Order mockOrder = new Order();
        mockOrder.setOrderId("123");
        mockOrder.setTotalAmount(100.0);
        mockOrder.setPaymentStatus(true);  // Assuming payment is successful

        when(orderService.completeOrder(userId)).thenReturn(mockOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/complete/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("123"))
                .andExpect(jsonPath("$.totalAmount").value(100.0))
                .andExpect(jsonPath("$.paymentStatus").value(true));
    }
}
