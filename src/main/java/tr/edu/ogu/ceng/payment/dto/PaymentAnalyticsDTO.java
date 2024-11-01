package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentAnalyticsDTO extends BaseDTO {
    private Long analyticsId;
    private BigDecimal totalPayments;
    private BigDecimal totalRefunds;
    private BigDecimal averageTransactionValue;
    private String paymentChannel;
    private LocalDateTime reportingDate;
}
