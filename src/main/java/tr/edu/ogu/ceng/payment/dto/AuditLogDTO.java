package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AuditLogDTO extends BaseDTO {
    private Long logId;
    private UUID userId;
    private String actionType;
    private LocalDateTime timestamp;
    private String description;
}
