package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.CurrencyDTO;
import tr.edu.ogu.ceng.payment.entity.Currency;
import tr.edu.ogu.ceng.payment.repository.CurrencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final ModelMapper modelMapper;

    public List<CurrencyDTO> findAll() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
                .map(currency -> modelMapper.map(currency, CurrencyDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<CurrencyDTO> findById(Long id) {
        return currencyRepository.findById(id)
                .map(currency -> modelMapper.map(currency, CurrencyDTO.class));
    }

    public CurrencyDTO save(CurrencyDTO currencyDTO) {
        Currency currency = modelMapper.map(currencyDTO, Currency.class);
        Currency savedCurrency = currencyRepository.save(currency);
        return modelMapper.map(savedCurrency, CurrencyDTO.class);
    }

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
