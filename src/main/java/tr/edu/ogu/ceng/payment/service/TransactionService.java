package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.TransactionDTO;
import tr.edu.ogu.ceng.payment.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    // Mevcut metodlar korundu
    // ...

    // Yeni metodlar eklendi
    public List<TransactionDTO> findTransactionsByPayment(Long paymentId) {
        return transactionRepository.findByPaymentPaymentId(paymentId)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findTransactionsByOrder(UUID orderId) {
        return transactionRepository.findByOrderId(orderId)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findTransactionsByAmountRange(
            String status, BigDecimal minAmount, BigDecimal maxAmount, LocalDateTime startDate) {
        return transactionRepository.findTransactionsByStatusAmountRangeAndDate(status, minAmount, maxAmount, startDate)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<Object[]> getTransactionSummaryByDate() {
        return transactionRepository.getTransactionSummaryByDate();
    }

    public BigDecimal calculateTotalAmountByCurrency(Long currencyId) {
        return transactionRepository.calculateTotalAmountByCurrency(currencyId);
    }

    public List<TransactionDTO> findRecentTransactions(int count) {
        return transactionRepository.findTopNByOrderByTransactionDateDesc(count)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findRecentFailedTransactions(LocalDateTime startDate) {
        return transactionRepository.findRecentFailedTransactions(startDate)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findAboveAverageTransactions() {
        return transactionRepository.findAboveAverageTransactions()
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    public Long countDailyTransactionsByOrder(UUID orderId) {
        return transactionRepository.countDailyTransactionsByOrder(orderId);
    }
}