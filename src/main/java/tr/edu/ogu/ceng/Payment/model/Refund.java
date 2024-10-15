package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refunds")
@NoArgsConstructor
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refund_id")
    private UUID refundId;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payment payment;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", length = 255)
    private String refundReason;

    @Column(length = 50)
    private String status;

    @Column(name = "refund_date", nullable = false)
    private LocalDateTime refundDate;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "refund_issued_at")
    private LocalDateTime refundIssuedAt;
}
