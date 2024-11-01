package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentFeeDTO extends BaseDTO {
    private Long feeId;
    private Long paymentId;
    private String feeType;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;
}
