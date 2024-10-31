package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CurrencyDTO {
    private Long id;  // Güncellenmiş Long id
    private String currencyName;
    private String symbol;
    private BigDecimal exchangeRate;
    private LocalDateTime lastUpdated;
}
