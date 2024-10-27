package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefundDTO {
    private Long refundId;
    private double refundAmount;
    private String refundReason;
    private String status;
    private LocalDateTime refundDate;
}
