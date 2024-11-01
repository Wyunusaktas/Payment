package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO extends BaseDTO {
    private Long transactionId;
    private Long paymentId;
    private UUID orderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime transactionDate;
    private LocalDateTime updatedAt;
}
