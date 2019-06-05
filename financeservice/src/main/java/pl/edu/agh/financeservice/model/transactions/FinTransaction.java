package pl.edu.agh.financeservice.model.transactions;

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

@Table(value = "fintransaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinTransaction {
    @PrimaryKeyColumn(
            name = "ftowner",
            type = PrimaryKeyType.PARTITIONED
    )
    private String ftowner;
    @PrimaryKeyColumn(
            name = "fttimestamp",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING
    )
    @Column
    private Date fttimestamp;
    @Column
    private BigDecimal amount;
    @Column
    private String fromaccount;
    @Column
    private String fromcurrency;
    @Column
    private BigDecimal ftamount;
    @Column
    private String id;
    @Column
    private BigDecimal originalamount;
    @Column
    private String receiver;
    @Column
    private String sender;
    @Column
    private String title;
    @Column
    private String toaccount;
    @Column
    private String tocurrency;
    @Column
    private String type;
}
