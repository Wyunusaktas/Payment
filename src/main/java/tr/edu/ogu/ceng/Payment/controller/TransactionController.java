package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Transaction;
import tr.edu.ogu.ceng.Payment.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Transaction> getTransaction(@PathVariable Long id) {
        return transactionService.findById(id);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        transaction.setTransactionId(id);  // ID'yi ayarla
        return transactionService.save(transaction);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeleteTransaction(@PathVariable Long id) {
        transactionService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
