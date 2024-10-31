package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class CurrencyDTO {
    private Long id;  // Güncellenmiş Long id
    private String currencyName;
    private String symbol;
}
