package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.TransactionHistory;
import tr.edu.ogu.ceng.Payment.service.TransactionHistoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction-history")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping
    public List<TransactionHistory> getAllTransactionHistories() {
        return transactionHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TransactionHistory> getTransactionHistory(@PathVariable Long id) {
        return transactionHistoryService.findById(id);
    }

    @PostMapping
    public TransactionHistory createTransactionHistory(@RequestBody TransactionHistory transactionHistory) {
        return transactionHistoryService.save(transactionHistory);
    }

    @PutMapping("/{id}")
    public TransactionHistory updateTransactionHistory(@PathVariable Long id, @RequestBody TransactionHistory transactionHistory) {
        transactionHistory.setHistoryId(id);  // ID'yi set et
        return transactionHistoryService.save(transactionHistory);
    }

    @DeleteMapping("/{id}")
    public void deleteTransactionHistory(@PathVariable Long id) {
        transactionHistoryService.deleteById(id);
    }
}
