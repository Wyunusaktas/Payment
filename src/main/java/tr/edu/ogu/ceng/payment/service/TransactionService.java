package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.TransactionDTO;
import tr.edu.ogu.ceng.payment.entity.Transaction;
import tr.edu.ogu.ceng.payment.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> findById(Long id) {
        return transactionRepository.findById(id)
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }

    public TransactionDTO save(TransactionDTO transactionDTO) {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            transaction.setDeletedAt(java.time.LocalDateTime.now());
            transaction.setDeletedBy(deletedBy);
            transactionRepository.save(transaction);
        }
    }
}
