package tr.edu.ogu.ceng.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_analytics")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class PaymentAnalytics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analytics_id")
    private Long analyticsId;  // UUID yerine Long

    @Column(name = "total_payments", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPayments;

    @Column(name = "total_refunds", precision = 10, scale = 2)
    private BigDecimal totalRefunds;

    @Column(name = "average_transaction_value", precision = 10, scale = 2)
    private BigDecimal averageTransactionValue;

    @Column(name = "payment_channel", length = 50)
    private String paymentChannel;

    @Column(name = "reporting_date", nullable = false)
    private LocalDateTime reportingDate;
}

