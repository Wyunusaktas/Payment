package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentAttemptDetailDTO {
    private Long attemptId;
    private Long userId;
    private Long paymentMethodId;
    private double amount;
    private String attemptStatus;
    private LocalDateTime attemptDate;
}
