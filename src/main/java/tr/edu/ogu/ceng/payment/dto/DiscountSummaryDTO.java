package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class DiscountSummaryDTO {
    private Long discountId;
    private String code;
    private double discountAmount;
}
