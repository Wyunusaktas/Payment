package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.TransactionHistory;
import tr.edu.ogu.ceng.Payment.repository.TransactionHistoryRepository;

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

    public void deleteById(Long id) {
        transactionHistoryRepository.deleteById(id);
    }
}
