package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionSummaryDTO {
    private Long transactionId;
    private String status;
    private double amount;
    private String currency;
    private LocalDateTime transactionDate;
}
