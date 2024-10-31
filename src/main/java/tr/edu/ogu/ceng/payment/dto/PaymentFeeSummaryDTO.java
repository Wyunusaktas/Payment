package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class PaymentFeeSummaryDTO {
    private Long feeId;
    private String feeType;
    private double amount;
    private String currency;
}
