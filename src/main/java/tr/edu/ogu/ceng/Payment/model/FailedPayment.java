package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "failed_payments")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class FailedPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "failed_payment_id")
    private Long failedPaymentId;  // UUID yerine Long

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "method_id")
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "attempt_date", nullable = false)
    private LocalDateTime attemptDate;
}

