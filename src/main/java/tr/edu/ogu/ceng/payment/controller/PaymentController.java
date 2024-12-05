package tr.edu.ogu.ceng.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tr.edu.ogu.ceng.payment.entity.Payment;
import tr.edu.ogu.ceng.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Kullanıcıya ait tüm ödemeleri getirir
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getAllPaymentsByUserId(@PathVariable UUID userId) {
        List<Payment> payments = paymentService.getAllPaymentsByUserId(userId);
        return payments.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Ödeme durumuna göre ödemeleri getirir
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return payments.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // PaymentMethod ile ödeme işlemlerini getirir (methodId)
    @GetMapping("/method/{methodId}")
    public ResponseEntity<List<Payment>> getPaymentsByPaymentMethod(@PathVariable UUID methodId) {
        List<Payment> payments = paymentService.getPaymentsByPaymentMethod(methodId);
        return payments.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Belirli bir tarih aralığındaki ödemeleri getirir
    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam LocalDateTime startDate, 
            @RequestParam LocalDateTime endDate) {
        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return payments.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Kullanıcıya ait belirli bir tarih aralığındaki ödemeleri getirir
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByUserIdAndDateRange(
            @PathVariable UUID userId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<Payment> payments = paymentService.getPaymentsByUserIdAndDateRange(userId, startDate, endDate);
        return payments.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Tüm ödemelerin toplam tutarını hesaplar
    @GetMapping("/total-amount")
    public ResponseEntity<BigDecimal> calculateTotalAmount() {
        BigDecimal totalAmount = paymentService.calculateTotalAmount();
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }

    // Belirli bir durumdaki ödemelerin toplam tutarını hesaplar
    @GetMapping("/total-amount/status/{status}")
    public ResponseEntity<BigDecimal> calculateTotalAmountByStatus(@PathVariable String status) {
        BigDecimal totalAmount = paymentService.calculateTotalAmountByStatus(status);
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }

    // Yeni ödeme kaydı ekler
    @PostMapping
    public ResponseEntity<Payment> addPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.addPayment(payment);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    // Ödeme kaydını günceller
    @PutMapping
    public ResponseEntity<Payment> updatePayment(@RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(payment);
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Ödeme kaydını siler
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
