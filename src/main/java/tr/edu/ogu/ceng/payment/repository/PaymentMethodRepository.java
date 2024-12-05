package tr.edu.ogu.ceng.payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    // Kullanıcıya ait tüm ödeme yöntemleri
    List<PaymentMethod> findByUserId(UUID userId);

    // Varsayılan ödeme yöntemi
    PaymentMethod findByUserIdAndIsDefaultTrue(UUID userId);

    // Ödeme türüne göre filtrele
    List<PaymentMethod> findByType(String type);

    // Sağlayıcıya göre filtrele
    List<PaymentMethod> findByProvider(String provider);
}
