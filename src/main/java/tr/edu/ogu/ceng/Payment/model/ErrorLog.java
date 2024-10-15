package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "error_logs")
@NoArgsConstructor
@Data
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "error_id")
    private UUID errorId;

    @Column(name = "error_message", nullable = false, length = 255)
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(nullable = false)
    private boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
