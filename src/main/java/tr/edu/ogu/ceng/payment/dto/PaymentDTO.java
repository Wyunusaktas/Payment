package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentDTO extends BaseDTO {
    private Long paymentId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String description;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionDate;
    private String externalReference;
    private double discountApplied;
    private double feeCharged;
    private boolean recurring;
    private String paymentChannel;
}
