package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiscountDTO {
    private Long discountId;
    private String code;
    private double discountAmount;
    private String discountType;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
