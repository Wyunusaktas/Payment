package tr.edu.ogu.ceng.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tr.edu.ogu.ceng.payment.restClientOrder.Order;
import tr.edu.ogu.ceng.payment.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint to complete an order for a given userId
    @PostMapping("/complete/{userId}")
    public Order completeOrder(@PathVariable String userId) {
        return orderService.completeOrder(userId);
    }
}
