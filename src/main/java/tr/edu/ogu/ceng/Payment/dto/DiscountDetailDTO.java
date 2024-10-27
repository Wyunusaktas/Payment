package tr.edu.ogu.ceng.Payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiscountDetailDTO {
    private Long discountId;
    private String code;
    private double discountAmount;
    private String discountType;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
