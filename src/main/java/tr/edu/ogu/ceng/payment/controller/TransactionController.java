package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.TransactionDTO;
import tr.edu.ogu.ceng.payment.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Temel CRUD operasyonları
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.save(transactionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDTO transactionDTO) {
        transactionDTO.setTransactionId(id);
        return ResponseEntity.ok(transactionService.save(transactionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.softDelete(id, "system");
        return ResponseEntity.noContent().build();
    }

    // Ödeme bazlı sorgular
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(transactionService.findTransactionsByPayment(paymentId));
    }

    // Sipariş bazlı sorgular
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(transactionService.findTransactionsByOrder(orderId));
    }

    @GetMapping("/order/{orderId}/daily-count")
    public ResponseEntity<Long> getDailyTransactionCountByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(transactionService.countDailyTransactionsByOrder(orderId));
    }

    // Durum bazlı sorgular
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(transactionService.findTransactionsByStatus(status));
    }

    @GetMapping("/failed")
    public ResponseEntity<List<TransactionDTO>> getRecentFailedTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        return ResponseEntity.ok(transactionService.findRecentFailedTransactions(startDate));
    }

    // Tarih bazlı sorgular
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.findTransactionsByDateRange(startDate, endDate));
    }

    // Tutar bazlı sorgular
    @GetMapping("/search/amount-range")
    public ResponseEntity<List<TransactionDTO>> searchTransactionsByAmountRange(
            @RequestParam String status,
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        return ResponseEntity.ok(
                transactionService.findTransactionsByAmountRange(status, minAmount, maxAmount, startDate)
        );
    }

    @GetMapping("/above-average")
    public ResponseEntity<List<TransactionDTO>> getAboveAverageTransactions() {
        return ResponseEntity.ok(transactionService.findAboveAverageTransactions());
    }

    // Analitik sorgular
    @GetMapping("/summary/daily")
    public ResponseEntity<List<Object[]>> getDailyTransactionSummary() {
        return ResponseEntity.ok(transactionService.getTransactionSummaryByDate());
    }

    @GetMapping("/currency/{currencyId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByCurrency(@PathVariable Long currencyId) {
        return ResponseEntity.ok(transactionService.calculateTotalAmountByCurrency(currencyId));
    }

    // Son işlemler
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(transactionService.findRecentTransactions(count));
    }
}