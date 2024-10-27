package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FailedPaymentDetailDTO {
    private Long failedPaymentId;
    private Long userId;
    private double amount;
    private String failureReason;
    private String paymentMethodId;
    private LocalDateTime attemptDate;
}
