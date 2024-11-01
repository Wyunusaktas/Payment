package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.CurrencyDTO;
import tr.edu.ogu.ceng.payment.service.CurrencyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CurrencyDTO> getCurrency(@PathVariable Long id) {
        return currencyService.findById(id);
    }

    @PostMapping
    public CurrencyDTO createCurrency(@RequestBody CurrencyDTO currencyDTO) {
        return currencyService.save(currencyDTO);
    }

    @PutMapping("/{id}")
    public CurrencyDTO updateCurrency(@PathVariable Long id, @RequestBody CurrencyDTO currencyDTO) {
        currencyDTO.setId(id);  // ID'yi ayarla
        return currencyService.save(currencyDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteCurrency(@PathVariable Long id) {
        currencyService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
