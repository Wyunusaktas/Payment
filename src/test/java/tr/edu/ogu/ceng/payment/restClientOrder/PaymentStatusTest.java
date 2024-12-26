package tr.edu.ogu.ceng.payment.restClientOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PaymentStatusTest {

    @Test
    void testEnumValues() {
        // Test that all enum constants are correctly defined
        PaymentStatus[] statuses = PaymentStatus.values();
        
        assertNotNull(statuses, "Enum constants should not be null");
        assertEquals(3, statuses.length, "There should be 3 enum constants");
        
        assertTrue(enumContains(statuses, PaymentStatus.SUCCESS), "Enum should contain SUCCESS");
        assertTrue(enumContains(statuses, PaymentStatus.FAILED), "Enum should contain FAILED");
        assertTrue(enumContains(statuses, PaymentStatus.PENDING), "Enum should contain PENDING");
    }

    @Test
    void testEnumValueOf() {
        // Test that each enum constant can be retrieved by its name
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
    }

    @Test
    void testEnumToString() {
        // Test the string representation of enum constants
        assertEquals("SUCCESS", PaymentStatus.SUCCESS.toString());
        assertEquals("FAILED", PaymentStatus.FAILED.toString());
        assertEquals("PENDING", PaymentStatus.PENDING.toString());
    }

    // Helper method to check if an enum array contains a certain value
    private boolean enumContains(PaymentStatus[] values, PaymentStatus status) {
        for (PaymentStatus value : values) {
            if (value == status) {
                return true;
            }
        }
        return false;
    }
}
