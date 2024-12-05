package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.TransactionHistory;
import tr.edu.ogu.ceng.payment.repository.TransactionHistoryRepository;

@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    // Kullanıcıya ait tüm işlem geçmişini getirir
    public List<TransactionHistory> getTransactionHistoryByUserId(UUID userId) {
        return transactionHistoryRepository.findByUserId(userId);
    }

    // Belirli bir ödeme ID'sine ait işlem geçmişini getirir
    public List<TransactionHistory> getTransactionHistoryByPaymentId(UUID paymentId) {
        return transactionHistoryRepository.findByPayment_PaymentId(paymentId);
    }

    // Belirli bir işlem türüne göre işlem geçmişini getirir
    public List<TransactionHistory> getTransactionHistoryByTransactionType(String transactionType) {
        return transactionHistoryRepository.findByTransactionType(transactionType);
    }

    // Belirli bir tarih aralığındaki işlem geçmişini getirir
    public List<TransactionHistory> getTransactionHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionHistoryRepository.findByTransactionDateBetween(startDate, endDate);
    }

    // Belirli bir kullanıcıya ait toplam işlem tutarını hesaplar
    public BigDecimal calculateTotalAmountByUserId(UUID userId) {
        return transactionHistoryRepository.calculateTotalAmountByUserId(userId);
    }

    // Yeni işlem geçmişi kaydı ekler
    public TransactionHistory addTransactionHistory(TransactionHistory transactionHistory) {
        return transactionHistoryRepository.save(transactionHistory);
    }

    // İşlem geçmişi kaydını günceller
    public TransactionHistory updateTransactionHistory(TransactionHistory transactionHistory) {
        if (transactionHistoryRepository.existsById(transactionHistory.getHistoryId())) {
            return transactionHistoryRepository.save(transactionHistory);
        }
        throw new IllegalArgumentException("İşlem geçmişi bulunamadı.");
    }

    // İşlem geçmişi kaydını siler
    public void deleteTransactionHistory(UUID transactionHistoryId) {
        if (transactionHistoryRepository.existsById(transactionHistoryId)) {
            transactionHistoryRepository.deleteById(transactionHistoryId);
        } else {
            throw new IllegalArgumentException("İşlem geçmişi bulunamadı.");
        }
    }
}
