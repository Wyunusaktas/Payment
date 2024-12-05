package tr.edu.ogu.ceng.payment.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_methods")
@NoArgsConstructor
@Data
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "method_id")
    private UUID methodId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "account_number", nullable = false, length = 100)
    private String accountNumber;

    @Column(name = "expiry_date", nullable = true)
    private LocalDate expiryDate;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
}
