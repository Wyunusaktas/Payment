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

    import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
    import tr.edu.ogu.ceng.payment.service.TransactionHistoryService;

    @RestController
    @RequestMapping("/api/transaction-history")
    public class TransactionHistoryController {

        private final TransactionHistoryService transactionHistoryService;

        @Autowired
        public TransactionHistoryController(TransactionHistoryService transactionHistoryService) {
            this.transactionHistoryService = transactionHistoryService;
        }

        // Kullanıcıya ait tüm işlem geçmişini getirir
        @GetMapping("/user/{userId}")
        public ResponseEntity<List<TransactionHistory>> getTransactionHistoryByUserId(@PathVariable UUID userId) {
            List<TransactionHistory> history = transactionHistoryService.getTransactionHistoryByUserId(userId);
            return history.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(history, HttpStatus.OK);
        }

        // Belirli bir ödeme ID'sine ait işlem geçmişini getirir
        @GetMapping("/payment/{paymentId}")
        public ResponseEntity<List<TransactionHistory>> getTransactionHistoryByPaymentId(@PathVariable UUID paymentId) {
            List<TransactionHistory> history = transactionHistoryService.getTransactionHistoryByPaymentId(paymentId);
            return history.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(history, HttpStatus.OK);
        }

        // Belirli bir işlem türüne göre işlem geçmişini getirir
        @GetMapping("/type/{transactionType}")
        public ResponseEntity<List<TransactionHistory>> getTransactionHistoryByTransactionType(@PathVariable String transactionType) {
            List<TransactionHistory> history = transactionHistoryService.getTransactionHistoryByTransactionType(transactionType);
            return history.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(history, HttpStatus.OK);
        }

        // Belirli bir tarih aralığındaki işlem geçmişini getirir
        @GetMapping("/date-range")
        public ResponseEntity<List<TransactionHistory>> getTransactionHistoryByDateRange(
                @RequestParam LocalDateTime startDate,
                @RequestParam LocalDateTime endDate) {
            List<TransactionHistory> history = transactionHistoryService.getTransactionHistoryByDateRange(startDate, endDate);
            return history.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(history, HttpStatus.OK);
        }

        // Belirli bir kullanıcıya ait toplam işlem tutarını hesaplar
        @GetMapping("/total/{userId}")
        public ResponseEntity<BigDecimal> calculateTotalAmountByUserId(@PathVariable UUID userId) {
            BigDecimal totalAmount = transactionHistoryService.calculateTotalAmountByUserId(userId);
            return new ResponseEntity<>(totalAmount, HttpStatus.OK);
        }

        // Yeni işlem geçmişi kaydı ekler
        @PostMapping
        public ResponseEntity<TransactionHistory> addTransactionHistory(@RequestBody TransactionHistory transactionHistory) {
            TransactionHistory savedTransactionHistory = transactionHistoryService.addTransactionHistory(transactionHistory);
            return new ResponseEntity<>(savedTransactionHistory, HttpStatus.CREATED);
        }

        // İşlem geçmişi kaydını günceller
        @PutMapping
        public ResponseEntity<TransactionHistory> updateTransactionHistory(@RequestBody TransactionHistory transactionHistory) {
            try {
                TransactionHistory updatedTransactionHistory = transactionHistoryService.updateTransactionHistory(transactionHistory);
                return new ResponseEntity<>(updatedTransactionHistory, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        // İşlem geçmişi kaydını siler
        @DeleteMapping("/{transactionHistoryId}")
        public ResponseEntity<Void> deleteTransactionHistory(@PathVariable UUID transactionHistoryId) {
            try {
                transactionHistoryService.deleteTransactionHistory(transactionHistoryId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }
