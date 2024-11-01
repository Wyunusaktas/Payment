package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FailedPaymentDTO extends BaseDTO {
    private Long failedPaymentId;
    private UUID userId;
    private BigDecimal amount;
    private String failureReason;
    private String paymentMethodId;
    private LocalDateTime attemptDate;
}
