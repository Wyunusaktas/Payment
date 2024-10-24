package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@NoArgsConstructor
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;  // UUID yerine Long

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 255)
    private String description;
}

