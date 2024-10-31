package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentSummaryDTO {
    private Long paymentId;
    private double amount;
    private String currency;
    private String status;
    private String paymentChannel;
    private LocalDateTime transactionDate;
}
