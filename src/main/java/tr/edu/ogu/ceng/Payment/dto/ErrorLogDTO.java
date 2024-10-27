package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorLogDTO {
    private Long errorId;
    private String errorMessage;
    private LocalDateTime occurredAt;
}
