package tr.edu.ogu.ceng.payment.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;
import tr.edu.ogu.ceng.payment.repository.PaymentMethodRepository;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    // Kullanıcıya ait tüm ödeme yöntemlerini getirir
    public List<PaymentMethod> getAllPaymentMethodsByUserId(UUID userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    // Kullanıcının varsayılan ödeme yöntemini getirir
    public PaymentMethod getDefaultPaymentMethod(UUID userId) {
        return paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId);
    }

    // Ödeme türüne göre ödeme yöntemlerini getirir
    public List<PaymentMethod> getPaymentMethodsByType(String type) {
        return paymentMethodRepository.findByType(type);
    }

    // Sağlayıcıya göre ödeme yöntemlerini getirir
    public List<PaymentMethod> getPaymentMethodsByProvider(String provider) {
        return paymentMethodRepository.findByProvider(provider);
    }

    // Yeni ödeme yöntemi ekler
    public PaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    // Ödeme yöntemini günceller
    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethodRepository.existsById(paymentMethod.getMethodId())) {
            return paymentMethodRepository.save(paymentMethod);
        }
        throw new IllegalArgumentException("Ödeme yöntemi bulunamadı.");
    }

    // Ödeme yöntemini siler
    public void deletePaymentMethod(UUID id) {
        if (paymentMethodRepository.existsById(id)) {
            paymentMethodRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Ödeme yöntemi bulunamadı.");
        }
    }

    // Kullanıcıya ait bir ödeme yöntemi var mı diye kontrol eder
    public boolean existsByUserId(UUID userId) {
        return !paymentMethodRepository.findByUserId(userId).isEmpty();
    }

    // Ödeme yöntemini ID ile getirir
    public Optional<PaymentMethod> getPaymentMethodById(UUID id) {
        return paymentMethodRepository.findById(id);
    }
}
