package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionDTO {
    private Long sessionId;
    private String ipAddress;
    private String device;
    private String location;
    private LocalDateTime loginTime;
}
