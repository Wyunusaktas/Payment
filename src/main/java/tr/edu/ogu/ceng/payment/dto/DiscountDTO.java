package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountDTO extends BaseDTO {
    private Long discountId;
    private String code;
    private BigDecimal discountAmount;
    private String discountType;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
