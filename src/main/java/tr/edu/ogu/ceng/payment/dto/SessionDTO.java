package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SessionDTO extends BaseDTO {
    private Long sessionId;
    private UUID userId;
    private String ipAddress;
    private String device;
    private String location;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String status;
}
