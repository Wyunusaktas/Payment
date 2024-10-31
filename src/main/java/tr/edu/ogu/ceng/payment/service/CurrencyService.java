package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.model.Currency;
import tr.edu.ogu.ceng.payment.repository.CurrencyRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findById(Long id) {
        return currencyRepository.findById(id);
    }

    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    // Soft delete işlemi için güncellenmiş metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Currency> currencyOptional = currencyRepository.findById(id);
        if (currencyOptional.isPresent()) {
            Currency currency = currencyOptional.get();
            currency.setDeletedAt(java.time.LocalDateTime.now());
            currency.setDeletedBy(deletedBy);
            currencyRepository.save(currency);
        }
    }
}
