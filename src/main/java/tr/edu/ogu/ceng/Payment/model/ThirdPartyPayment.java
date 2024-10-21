package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "third_party_payments")
@NoArgsConstructor
@Data
public class ThirdPartyPayment {

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

