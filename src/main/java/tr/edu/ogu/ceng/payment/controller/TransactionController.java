package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.TransactionDTO;
import tr.edu.ogu.ceng.payment.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TransactionDTO> getTransaction(@PathVariable Long id) {
        return transactionService.findById(id);
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return transactionService.save(transactionDTO);
    }

    @PutMapping("/{id}")
    public TransactionDTO updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO.setTransactionId(id);  // ID'yi ayarla
        return transactionService.save(transactionDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteTransaction(@PathVariable Long id) {
        transactionService.softDelete(id, "system");
    }
}
