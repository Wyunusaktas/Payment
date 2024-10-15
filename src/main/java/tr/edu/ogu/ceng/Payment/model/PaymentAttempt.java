package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_attempts")
@NoArgsConstructor
@Data
public class PaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attempt_id")
    private UUID attemptId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "method_id")
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "attempt_status", length = 50, nullable = false)
    private String attemptStatus;

    @Column(name = "attempt_date", nullable = false)
    private LocalDateTime attemptDate;

    @Column(name = "error_message", length = 255)
    private String errorMessage;
}

