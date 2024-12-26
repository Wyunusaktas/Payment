package tr.edu.ogu.ceng.payment.restClientOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void testUsernameGetterAndSetter() {
        User user = new User();
        
        // Set a value for username
        String expectedUsername = "john_doe";
        user.setUsername(expectedUsername);
        
        // Assert that the getter returns the same value
        assertEquals(expectedUsername, user.getUsername(), "Username should be 'john_doe'");
    }

    @Test
    void testFirstnameGetterAndSetter() {
        User user = new User();
        
        // Set a value for firstname
        String expectedFirstname = "John";
        user.setFirstname(expectedFirstname);
        
        // Assert that the getter returns the same value
        assertEquals(expectedFirstname, user.getFirstname(), "Firstname should be 'John'");
    }

    @Test
    void testLastnameGetterAndSetter() {
        User user = new User();
        
        // Set a value for lastname
        String expectedLastname = "Doe";
        user.setLastname(expectedLastname);
        
        // Assert that the getter returns the same value
        assertEquals(expectedLastname, user.getLastname(), "Lastname should be 'Doe'");
    }

    @Test
    void testSetAndGetUserInfo() {
        User user = new User();
        
        // Set values for all fields
        user.setUsername("jane_doe");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        
        // Assert all getters return the correct values
        assertEquals("jane_doe", user.getUsername(), "Username should be 'jane_doe'");
        assertEquals("Jane", user.getFirstname(), "Firstname should be 'Jane'");
        assertEquals("Doe", user.getLastname(), "Lastname should be 'Doe'");
    }
}
