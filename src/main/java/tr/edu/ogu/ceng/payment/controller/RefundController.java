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

import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.service.RefundService;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    @Autowired
    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    // Belirli bir ödeme ID'sine ait iadeleri getirir
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<Refund>> getRefundsByPaymentId(@PathVariable UUID paymentId) {
        List<Refund> refunds = refundService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(refunds);
    }

    // İade durumuna göre iadeleri getirir
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Refund>> getRefundsByStatus(@PathVariable String status) {
        List<Refund> refunds = refundService.getRefundsByStatus(status);
        return new ResponseEntity<>(refunds, HttpStatus.OK);
    }

    // Belirli bir tarih aralığındaki iadeleri getirir
    @GetMapping("/date-range")
    public ResponseEntity<List<Refund>> getRefundsByDateRange(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        List<Refund> refunds = refundService.getRefundsByDateRange(startDate, endDate);
        return new ResponseEntity<>(refunds, HttpStatus.OK);
    }

    // Tüm iadelerin toplam tutarını hesaplar
    @GetMapping("/total-amount")
    public ResponseEntity<BigDecimal> calculateTotalRefundAmount() {
        BigDecimal totalAmount = refundService.calculateTotalRefundAmount();
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }

    // Belirli bir durumdaki iadelerin toplam tutarını hesaplar
    @GetMapping("/total-amount/status/{status}")
    public ResponseEntity<BigDecimal> calculateTotalRefundAmountByStatus(@PathVariable String status) {
        BigDecimal totalAmount = refundService.calculateTotalRefundAmountByStatus(status);
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }

    // Yeni iade kaydı ekler
    @PostMapping
    public ResponseEntity<Refund> addRefund(@RequestBody Refund refund) {
        Refund savedRefund = refundService.addRefund(refund);
        return new ResponseEntity<>(savedRefund, HttpStatus.CREATED);
    }

    // İade kaydını günceller
    @PutMapping
    public ResponseEntity<Refund> updateRefund(@RequestBody Refund refund) {
        try {
            Refund updatedRefund = refundService.updateRefund(refund);
            return new ResponseEntity<>(updatedRefund, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // İade kaydını siler
    @DeleteMapping("/{refundId}")
    public ResponseEntity<Void> deleteRefund(@PathVariable UUID refundId) {
        try {
            refundService.deleteRefund(refundId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
