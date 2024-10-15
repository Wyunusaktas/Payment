package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_analytics")
@NoArgsConstructor
@Data
public class PaymentAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "analytics_id")
    private UUID analyticsId;

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

