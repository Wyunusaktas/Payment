package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public abstract class BaseDTO {
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String deletedBy;
    private LocalDateTime deletedAt;
    private Long version;
}
