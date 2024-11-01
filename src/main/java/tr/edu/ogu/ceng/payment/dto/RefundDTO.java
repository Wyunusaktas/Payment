package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundDTO extends BaseDTO {
    private Long refundId;
    private BigDecimal refundAmount;
    private String refundReason;
    private String status;
    private LocalDateTime refundDate;
    private String refundMethod;
    private LocalDateTime refundIssuedAt;
}
