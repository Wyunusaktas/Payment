package tr.edu.ogu.ceng.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.Transaction;
import tr.edu.ogu.ceng.payment.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Belirli bir ödeme ID'sine ait tüm işlemleri getirir
    public List<Transaction> getTransactionsByPaymentId(UUID paymentId) {
        return transactionRepository.findByPayment_PaymentId(paymentId);
    }

    // İşlem durumuna göre işlemleri getirir
    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    // Belirli bir tarih aralığındaki işlemleri getirir
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    // Tüm işlemlerin toplam tutarını hesaplar
    public BigDecimal calculateTotalTransactionAmount() {
        return transactionRepository.calculateTotalTransactionAmount();
    }

    // Belirli bir ödeme ID'sine ait toplam işlem tutarını hesaplar
    public BigDecimal calculateTotalAmountByPaymentId(UUID paymentId) {
        return transactionRepository.calculateTotalAmountByPaymentId(paymentId);
    }

    // Yeni işlem kaydı ekler
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // İşlem kaydını günceller
    public Transaction updateTransaction(Transaction transaction) {
        if (transactionRepository.existsById(transaction.getTransactionId())) {
            return transactionRepository.save(transaction);
        }
        throw new IllegalArgumentException("İşlem bulunamadı.");
    }

    // İşlem kaydını siler
    public void deleteTransaction(UUID transactionId) {
        if (transactionRepository.existsById(transactionId)) {
            transactionRepository.deleteById(transactionId);
        } else {
            throw new IllegalArgumentException("İşlem bulunamadı.");
        }
    }
}
