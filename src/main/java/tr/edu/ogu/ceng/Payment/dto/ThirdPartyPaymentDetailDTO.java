package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThirdPartyPaymentDetailDTO {
    private Long thirdPartyPaymentId;
    private Long paymentId;
    private String provider;
    private String transactionReference;
    private String status;
    private LocalDateTime processedAt;
}
