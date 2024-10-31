package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FraudDetectionDTO {
    private Long fraudCaseId;
    private Long paymentId;
    private Long userId;
    private String suspiciousReason;
    private double fraudScore;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}
