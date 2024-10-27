package tr.edu.ogu.ceng.Payment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currencies")
@NoArgsConstructor
@Data
@Where(clause = "deleted_at IS NULL")
public class Currency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_code")
    private Long id;  // UUID yerine Long

    @Column(name = "currency_name", nullable = false, length = 50)
    private String currencyName;

    @Column(length = 5)
    private String symbol;

    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

