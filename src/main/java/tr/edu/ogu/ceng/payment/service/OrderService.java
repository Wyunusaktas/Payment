package tr.edu.ogu.ceng.payment.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import tr.edu.ogu.ceng.payment.restClientOrder.Order;
import tr.edu.ogu.ceng.payment.restClientOrder.PaymentStatus;
import tr.edu.ogu.ceng.payment.restClientOrder.User;
@RequiredArgsConstructor
@Service
public class OrderService {
    private final RestClient restClient;

    // Fetch user details
    public User getUser() {
        return restClient.get()
                .uri("http://192.168.137.195:8007/api/users/testuser")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(User.class);
    }

    // Fetch the order details for a specific user
    public Order getOrder(String userId) {
        return restClient.get()
                .uri("http://192.168.137.169:8003/api/order/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Order.class);
    }


    public Order completeOrder(String userId) {
        User user = getUser();
        Order order = getOrder(userId);
        PaymentStatus paymentStatus = processPayment(order.getTotalAmount());
        order.setPaymentStatus(paymentStatus == PaymentStatus.SUCCESS);
        return sendFeedbackToOrderSystem(order);
    }

    // Simulate payment process
    private PaymentStatus processPayment(double totalAmount) {
        return PaymentStatus.SUCCESS;
    }

    // Send feedback to the order system by updating the order status
    private Order sendFeedbackToOrderSystem(Order order) {
        return restClient.put()
                .uri("http://192.168.137.169:8003/api/order/{orderId}", order.getOrderId())
                .accept(MediaType.APPLICATION_JSON)
                .body(order)  // Send the updated order
                .retrieve()
                .body(Order.class);
    }
}
