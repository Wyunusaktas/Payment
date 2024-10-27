package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;

@Data
public class TransactionHistorySummaryDTO {
    private Long historyId;
    private double amount;
    private String transactionType;
    private String status;
}
