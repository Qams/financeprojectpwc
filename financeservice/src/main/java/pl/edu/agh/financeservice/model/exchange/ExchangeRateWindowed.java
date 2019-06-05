package pl.edu.agh.financeservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;

@Table(value = "exchangeratewindowed")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateWindowed {

    @PrimaryKeyColumn(
            name = "exchange",
            type = PrimaryKeyType.PARTITIONED
    )
    private String exchange;
    @PrimaryKeyColumn(
            name = "endtime",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING
    )
    @Column
    private Date endtime;
    @Column
    private Long changes;
    @Column
    private Double maxbid;
    @Column
    private Double maxask;
    @Column
    private Double minbid;
    @Column
    private String minask;
    @Column
    private String starttime;

}
