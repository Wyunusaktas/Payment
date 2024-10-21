package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Currency;
import tr.edu.ogu.ceng.Payment.repository.CurrencyRepository;

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

    public void deleteById(Long id) {
        currencyRepository.deleteById(id);
    }
}
