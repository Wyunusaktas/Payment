package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.model.Discount;
import tr.edu.ogu.ceng.payment.service.DiscountService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discount")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Discount> getDiscount(@PathVariable Long id) {
        return discountService.findById(id);
    }

    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount) {
        return discountService.save(discount);
    }

    @PutMapping("/{id}")
    public Discount updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {
        discount.setDiscountId(id);  // ID'yi set et
        return discountService.save(discount);
    }

    // Soft delete işlemi için güncellenmiş endpoint
    @DeleteMapping("/{id}")
    public void softDeleteDiscount(@PathVariable Long id) {
        discountService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
