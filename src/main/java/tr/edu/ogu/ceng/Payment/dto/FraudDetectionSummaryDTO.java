package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FraudDetectionSummaryDTO {
    private Long fraudCaseId;
    private String suspiciousReason;
    private String status;
    private double fraudScore;
    private LocalDateTime reportedAt;
}
