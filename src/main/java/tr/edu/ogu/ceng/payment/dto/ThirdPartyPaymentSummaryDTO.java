package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class ThirdPartyPaymentSummaryDTO {
    private Long thirdPartyPaymentId;
    private String provider;
    private String status;
}
