package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDetailDTO {
    private Long transactionId;
    private Long paymentId;
    private Long orderId;
    private String status;
    private double amount;
    private String currency;
    private String description;
    private LocalDateTime transactionDate;
}
