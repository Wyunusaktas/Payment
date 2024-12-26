package tr.edu.ogu.ceng.payment.restClientOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        // Initialize the Order object before each test
        order = new Order();
    }

    @Test
    void testSetOrderId() {
        String orderId = "ORD123";
        order.setOrderId(orderId);
        assertEquals(orderId, order.getOrderId(), "The order ID should be set correctly.");
    }

    @Test
    void testSetUserId() {
        String userId = "USER456";
        order.setUserId(userId);
        assertEquals(userId, order.getUserId(), "The user ID should be set correctly.");
    }

    @Test
    void testSetTotalAmount() {
        double totalAmount = 99.99;
        order.setTotalAmount(totalAmount);
        assertEquals(totalAmount, order.getTotalAmount(), "The total amount should be set correctly.");
    }

    @Test
    void testSetPaymentStatus() {
        Boolean paymentStatus = true;
        order.setPaymentStatus(paymentStatus);
        assertEquals(paymentStatus, order.getPaymentStatus(), "The payment status should be set correctly.");
    }

    @Test
    void testDefaultValues() {
        // Test the default values of the Order object
        assertNull(order.getOrderId(), "The default order ID should be null.");
        assertNull(order.getUserId(), "The default user ID should be null.");
        assertEquals(0.0, order.getTotalAmount(), "The default total amount should be 0.0.");
        assertNull(order.getPaymentStatus(), "The default payment status should be null.");
    }
}
