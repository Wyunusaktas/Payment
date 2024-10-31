package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefundDTO {
    private Long refundId;
    private double refundAmount;
    private String refundReason;
    private String status;
    private LocalDateTime refundDate;
    private String refundMethod;
    private LocalDateTime refundIssuedAt;
}
