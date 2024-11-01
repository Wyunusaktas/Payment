package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentMethodDTO extends BaseDTO {
    private Long methodId;
    private UUID userId;
    private String type;
    private String provider;
    private String accountNumber;
    private LocalDate expiryDate;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
