package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionHistoryDTO extends BaseDTO {
    private Long historyId;
    private UUID userId;
    private Long paymentId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;
}
