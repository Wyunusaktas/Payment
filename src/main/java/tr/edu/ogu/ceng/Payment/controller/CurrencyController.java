package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Currency;
import tr.edu.ogu.ceng.Payment.service.CurrencyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Currency> getCurrency(@PathVariable Long id) {
        return currencyService.findById(id);
    }

    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    @PutMapping("/{id}")
    public Currency updateCurrency(@PathVariable Long id, @RequestBody Currency currency) {
        currency.setId(id);  // ID'yi ayarla
        return currencyService.save(currency);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeleteCurrency(@PathVariable Long id) {
        currencyService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
