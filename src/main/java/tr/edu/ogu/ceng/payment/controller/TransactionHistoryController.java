package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.TransactionHistoryDTO;
import tr.edu.ogu.ceng.payment.service.TransactionHistoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction-history")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping
    public List<TransactionHistoryDTO> getAllTransactionHistories() {
        return transactionHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TransactionHistoryDTO> getTransactionHistory(@PathVariable Long id) {
        return transactionHistoryService.findById(id);
    }

    @PostMapping
    public TransactionHistoryDTO createTransactionHistory(@RequestBody TransactionHistoryDTO transactionHistoryDTO) {
        return transactionHistoryService.save(transactionHistoryDTO);
    }

    @PutMapping("/{id}")
    public TransactionHistoryDTO updateTransactionHistory(@PathVariable Long id, @RequestBody TransactionHistoryDTO transactionHistoryDTO) {
        transactionHistoryDTO.setHistoryId(id);  // ID'yi ayarla
        return transactionHistoryService.save(transactionHistoryDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteTransactionHistory(@PathVariable Long id) {
        transactionHistoryService.softDelete(id, "system");
    }
}
