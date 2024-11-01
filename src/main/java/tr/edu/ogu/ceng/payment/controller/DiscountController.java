package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.DiscountDTO;
import tr.edu.ogu.ceng.payment.service.DiscountService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discount")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    public List<DiscountDTO> getAllDiscounts() {
        return discountService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<DiscountDTO> getDiscount(@PathVariable Long id) {
        return discountService.findById(id);
    }

    @PostMapping
    public DiscountDTO createDiscount(@RequestBody DiscountDTO discountDTO) {
        return discountService.save(discountDTO);
    }

    @PutMapping("/{id}")
    public DiscountDTO updateDiscount(@PathVariable Long id, @RequestBody DiscountDTO discountDTO) {
        discountDTO.setDiscountId(id);  // ID'yi set et
        return discountService.save(discountDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteDiscount(@PathVariable Long id) {
        discountService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
