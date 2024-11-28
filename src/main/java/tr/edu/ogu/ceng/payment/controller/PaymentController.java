package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.PaymentDTO;
import tr.edu.ogu.ceng.payment.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Mevcut endpoint'ler korundu
    // ...

    // Yeni endpoint'ler eklendi
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.findPaymentsByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(paymentService.findPaymentsByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.findPaymentsByDateRange(startDate, endDate));
    }

    @GetMapping("/amount-above/{amount}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsAboveAmount(@PathVariable BigDecimal amount) {
        return ResponseEntity.ok(paymentService.findPaymentsAboveAmount(amount));
    }

    @GetMapping("/method/{methodId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByMethod(@PathVariable Long methodId) {
        return ResponseEntity.ok(paymentService.findPaymentsByMethod(methodId));
    }

    @GetMapping("/recurring")
    public ResponseEntity<List<PaymentDTO>> getRecurringPayments() {
        return ResponseEntity.ok(paymentService.findRecurringPayments());
    }

    @GetMapping("/discounted")
    public ResponseEntity<List<PaymentDTO>> getDiscountedPayments() {
        return ResponseEntity.ok(paymentService.findDiscountedPayments());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PaymentDTO>> searchPayments(
            @RequestParam String status,
            @RequestParam BigDecimal minAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        return ResponseEntity.ok(paymentService.findPaymentsByStatusAmountAndDate(status, minAmount, startDate));
    }

    @GetMapping("/provider/{provider}/completed")
    public ResponseEntity<List<PaymentDTO>> getCompletedPaymentsByProvider(@PathVariable String provider) {
        return ResponseEntity.ok(paymentService.findCompletedPaymentsByProvider(provider));
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalPaymentAmountByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.calculateTotalPaymentAmountByUser(userId));
    }

    @GetMapping("/user/{userId}/last")
    public ResponseEntity<PaymentDTO> getLastPaymentByUser(@PathVariable UUID userId) {
        return paymentService.findLastPaymentByUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/successful")
    public ResponseEntity<List<PaymentDTO>> getSuccessfulPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.findSuccessfulPaymentsInPeriod(startDate, endDate));
    }

    @GetMapping("/high-value")
    public ResponseEntity<List<PaymentDTO>> getHighValuePayments() {
        return ResponseEntity.ok(paymentService.findHighValuePayments());
    }
}