package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import tr.edu.ogu.ceng.payment.restClientOrder.Order;
import tr.edu.ogu.ceng.payment.restClientOrder.PaymentStatus;
import tr.edu.ogu.ceng.payment.restClientOrder.User;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private RestClient restClient; // RestClient Mock'ı

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec; // RestClient RequestHeadersUriSpec Mock'ı

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec; // RestClient RequestBodyUriSpec Mock'ı

    @Mock
    private RestClient.ResponseSpec responseSpec; // RestClient ResponseSpec Mock'ı

    @InjectMocks
    private OrderService orderService; // Sınıfı test etmek için

    @Test
    void testCompleteOrder() {
        // Veriler hazırlıyoruz
        String userId = "testuser";

        User mockUser = new User();
        mockUser.setId(userId);

        Order mockOrder = new Order();
        mockOrder.setOrderId("1L");
        mockOrder.setTotalAmount(100.0);

        // RestClient'ı mockluyoruz
        when(restClient.get()).thenReturn(requestHeadersUriSpec); // get() metodu çağrıldığında requestHeadersUriSpec dönsün
        when(requestHeadersUriSpec.uri("http://192.168.137.195:8007/api/users/testuser"))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.accept(MediaType.APPLICATION_JSON))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve())
            .thenReturn(responseSpec);

        // Burada body() metodunu kullanıyoruz, bodyToMono() yerine
        when(responseSpec.body(User.class))
            .thenReturn(mockUser); // Doğrudan mockUser dönüyoruz

        when(restClient.get()).thenReturn(requestHeadersUriSpec); // get() tekrar çağrılıyor
        when(requestHeadersUriSpec.uri("http://192.168.137.169:8003/api/order/{userId}", userId))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.accept(MediaType.APPLICATION_JSON))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve())
            .thenReturn(responseSpec);

        // Burada body() metodunu kullanıyoruz
        when(responseSpec.body(Order.class))
            .thenReturn(mockOrder); // Doğrudan mockOrder dönüyoruz

        when(restClient.put()).thenReturn(requestBodyUriSpec); // put() metodu çağrıldığında requestBodyUriSpec dönsün
        when(requestBodyUriSpec.uri("http://192.168.137.169:8003/api/order/{orderId}", mockOrder.getOrderId()))
            .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.accept(MediaType.APPLICATION_JSON))
            .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(mockOrder)) // Mock nesne gönderiliyor
            .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve())
            .thenReturn(responseSpec);
        when(responseSpec.body(Order.class))
            .thenReturn(mockOrder);

        // OrderService'in metodunu çağırıyoruz
        Order completedOrder = orderService.completeOrder(userId);

        // Sonuçları kontrol ediyoruz
        assertNotNull(completedOrder);
        assertEquals(mockOrder.getOrderId(), completedOrder.getOrderId());
        assertTrue(completedOrder.getPaymentStatus());
    }
}
