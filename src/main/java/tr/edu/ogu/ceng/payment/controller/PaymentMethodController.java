package tr.edu.ogu.ceng.payment.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.service.PaymentMethodService;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    // Kullanıcıya ait tüm ödeme yöntemlerini getirir
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethodsByUserId(@PathVariable UUID userId) {
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethodsByUserId(userId);
        return paymentMethods.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    // Kullanıcının varsayılan ödeme yöntemini getirir
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<PaymentMethod> getDefaultPaymentMethod(@PathVariable UUID userId) {
        PaymentMethod paymentMethod = paymentMethodService.getDefaultPaymentMethod(userId);
        return paymentMethod == null ? 
            new ResponseEntity<>(HttpStatus.NOT_FOUND) : 
            new ResponseEntity<>(paymentMethod, HttpStatus.OK);
    }

    // Ödeme türüne göre ödeme yöntemlerini getirir
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByType(@PathVariable String type) {
        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByType(type);
        return paymentMethods.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    // Sağlayıcıya göre ödeme yöntemlerini getirir
    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByProvider(@PathVariable String provider) {
        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByProvider(provider);
        return paymentMethods.isEmpty() ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    // Yeni ödeme yöntemi ekler
    @PostMapping
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        PaymentMethod savedPaymentMethod = paymentMethodService.addPaymentMethod(paymentMethod);
        return new ResponseEntity<>(savedPaymentMethod, HttpStatus.CREATED);
    }

    // Ödeme yöntemini günceller
    @PutMapping
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        try {
            PaymentMethod updatedPaymentMethod = paymentMethodService.updatePaymentMethod(paymentMethod);
            return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Ödeme yöntemini siler
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable UUID id) {
        try {
            paymentMethodService.deletePaymentMethod(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Kullanıcıya ait bir ödeme yöntemi olup olmadığını kontrol eder
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable UUID userId) {
        boolean exists = paymentMethodService.existsByUserId(userId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Ödeme yöntemini ID ile getirir
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable UUID id) {
        Optional<PaymentMethod> paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return paymentMethod.isPresent() ? 
            new ResponseEntity<>(paymentMethod.get(), HttpStatus.OK) : 
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
}
