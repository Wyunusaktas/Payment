package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class ChargebackSummaryDTO {
    private Long chargebackId;
    private double chargebackAmount;
    private String reason;
    private String status;
}
