package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThirdPartyPaymentDTO extends BaseDTO {
    private Long thirdPartyPaymentId;
    private Long paymentId;
    private String provider;
    private String transactionReference;
    private String status;
    private LocalDateTime processedAt;
}
