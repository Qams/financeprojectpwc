package pl.edu.agh.financeservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table(value = "exchangerate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
    @PrimaryKeyColumn(
            name = "exchange",
            type = PrimaryKeyType.PARTITIONED
    )
    private String exchange;
    @PrimaryKeyColumn(
            name = "currencytime",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING
    )
    @Column
    private Date currencytime;
    @Column
    private BigDecimal ask;
    @Column
    private BigDecimal bid;
    @Column
    private BigDecimal askvolume;
    @Column
    private BigDecimal bidvolume;
    @Column
    private String fromcurrency;
    @Column
    private String tocurrency;
    @Column
    private String id;
}
