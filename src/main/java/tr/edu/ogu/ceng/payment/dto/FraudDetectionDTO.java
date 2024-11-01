package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FraudDetectionDTO extends BaseDTO {
    private Long fraudCaseId;
    private Long paymentId;
    private UUID userId;
    private String suspiciousReason;
    private BigDecimal fraudScore;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}
