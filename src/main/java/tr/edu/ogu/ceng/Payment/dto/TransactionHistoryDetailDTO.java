package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryDetailDTO {
    private Long historyId;
    private Long userId;
    private Long paymentId;
    private String transactionType;
    private double amount;
    private LocalDateTime transactionDate;
    private String status;
}
