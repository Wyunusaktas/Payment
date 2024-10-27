package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FailedPaymentSummaryDTO {
    private Long failedPaymentId;
    private double amount;
    private String failureReason;
    private LocalDateTime attemptDate;
}
