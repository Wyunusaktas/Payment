package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "currency", referencedColumnName = "currency_code")
    private Currency currency;

    @Column(nullable = false, length = 50)
    private String status;

    @ManyToOne
    @JoinColumn(name = "payment_method", referencedColumnName = "method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "external_reference", length = 255)
    private String externalReference;

    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied;

    @Column(name = "fee_charged", precision = 10, scale = 2)
    private BigDecimal feeCharged;

    @Column(nullable = false)
    private boolean recurring;

    @Column(name = "payment_channel", length = 50)
    private String paymentChannel;
}

