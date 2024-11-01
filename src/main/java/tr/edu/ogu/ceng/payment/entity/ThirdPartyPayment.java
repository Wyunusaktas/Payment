package tr.edu.ogu.ceng.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;


import java.time.LocalDateTime;

@Entity
@Table(name = "third_party_payments")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class ThirdPartyPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "third_party_payment_id")
    private Long thirdPartyPaymentId;  // UUID yerine Long

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payment payment;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "transaction_reference", length = 255)
    private String transactionReference;

    @Column(length = 50)
    private String status;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}

