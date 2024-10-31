package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.model.TransactionHistory;
import tr.edu.ogu.ceng.payment.repository.TransactionHistoryRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    public List<TransactionHistory> findAll() {
        return transactionHistoryRepository.findAll();
    }

    public Optional<TransactionHistory> findById(Long id) {
        return transactionHistoryRepository.findById(id);
    }

    public TransactionHistory save(TransactionHistory transactionHistory) {
        return transactionHistoryRepository.save(transactionHistory);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<TransactionHistory> transactionHistoryOptional = transactionHistoryRepository.findById(id);
        if (transactionHistoryOptional.isPresent()) {
            TransactionHistory transactionHistory = transactionHistoryOptional.get();
            transactionHistory.setDeletedAt(java.time.LocalDateTime.now());
            transactionHistory.setDeletedBy(deletedBy);
            transactionHistoryRepository.save(transactionHistory);
        }
    }
}
