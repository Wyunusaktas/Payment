package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_fees")
@NoArgsConstructor
@Data
public class PaymentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "fee_id")
    private UUID feeId;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payment payment;

    @Column(name = "fee_type", length = 50, nullable = false)
    private String feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "currency", referencedColumnName = "currency_code")
    private Currency currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

