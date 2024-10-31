package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PaymentMethodDTO {
    private Long methodId;
    private UUID userId;
    private String type;
    private String provider;
    private String accountNumber;
    private LocalDate expiryDate;
    private boolean isDefault;
}
