package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentAttemptDTO extends BaseDTO {
    private Long attemptId;
    private UUID userId;
    private Long paymentMethodId;
    private BigDecimal amount;
    private String attemptStatus;
    private LocalDateTime attemptDate;
    private String errorMessage;
}
