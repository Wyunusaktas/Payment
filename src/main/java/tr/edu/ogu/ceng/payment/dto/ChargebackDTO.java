package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChargebackDTO extends BaseDTO {
    private Long chargebackId;
    private Long paymentId;
    private UUID userId;
    private BigDecimal chargebackAmount;
    private String reason;
    private String status;
    private LocalDateTime filedAt;
    private LocalDateTime resolvedAt;
}
