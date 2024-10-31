package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FailedPaymentDTO {
    private Long failedPaymentId;
    private Long userId;
    private double amount;
    private String failureReason;
    private String paymentMethodId;
    private LocalDateTime attemptDate;
}
