package tr.edu.ogu.ceng.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_fees")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class PaymentFee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Long feeId;  // UUID yerine Long

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

