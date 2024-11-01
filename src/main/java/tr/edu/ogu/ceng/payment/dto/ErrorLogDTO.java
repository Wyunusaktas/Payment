package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorLogDTO extends BaseDTO {
    private Long errorId;
    private String errorMessage;
    private String stackTrace;
    private LocalDateTime occurredAt;
    private boolean resolved;
    private LocalDateTime resolvedAt;
}
