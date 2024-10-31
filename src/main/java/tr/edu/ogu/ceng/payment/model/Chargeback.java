package tr.edu.ogu.ceng.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chargebacks")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class Chargeback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chargeback_id")
    private Long chargebackId;  // UUID yerine Long

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payment payment;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "chargeback_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal chargebackAmount;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "filed_at", nullable = false)
    private LocalDateTime filedAt;

    @Column(length = 50, nullable = false)
    private String status;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}

