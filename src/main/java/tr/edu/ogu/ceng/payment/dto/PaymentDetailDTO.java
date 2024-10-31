package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDetailDTO {
    private Long paymentId;
    private Long userId;
    private double amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String description;
    private LocalDateTime transactionDate;
    private String externalReference;
    private double discountApplied;
    private double feeCharged;
    private boolean recurring;
}
