package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChargebackDetailDTO {
    private Long chargebackId;
    private Long paymentId;
    private Long userId;
    private double chargebackAmount;
    private String reason;
    private String status;
    private LocalDateTime filedAt;
    private LocalDateTime resolvedAt;
}
