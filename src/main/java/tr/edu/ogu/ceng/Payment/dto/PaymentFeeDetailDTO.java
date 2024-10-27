package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentFeeDetailDTO {
    private Long feeId;
    private Long paymentId;
    private String feeType;
    private double amount;
    private String currency;
    private LocalDateTime createdAt;
}
