package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private Long logId;
    private String actionType;
    private LocalDateTime timestamp;
    private String description;
}
