package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentAnalyticsDTO {
    private Long analyticsId;
    private double totalPayments;
    private double totalRefunds;
    private double averageTransactionValue;
    private String paymentChannel;
    private LocalDateTime reportingDate;
}
