package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.TransactionHistoryDTO;
import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
import tr.edu.ogu.ceng.payment.repository.TransactionHistoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ModelMapper modelMapper;

    public List<TransactionHistoryDTO> findAll() {
        return transactionHistoryRepository.findAll()
                .stream()
                .map(transactionHistory -> modelMapper.map(transactionHistory, TransactionHistoryDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<TransactionHistoryDTO> findById(Long id) {
        return transactionHistoryRepository.findById(id)
                .map(transactionHistory -> modelMapper.map(transactionHistory, TransactionHistoryDTO.class));
    }

    public TransactionHistoryDTO save(TransactionHistoryDTO transactionHistoryDTO) {
        TransactionHistory transactionHistory = modelMapper.map(transactionHistoryDTO, TransactionHistory.class);
        TransactionHistory savedTransactionHistory = transactionHistoryRepository.save(transactionHistory);
        return modelMapper.map(savedTransactionHistory, TransactionHistoryDTO.class);
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
