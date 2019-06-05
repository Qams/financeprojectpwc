package pl.edu.agh.financeservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRateStats {
    private double minbid;
    private double maxbid;
    private double minask;
    private double maxask;
    private long changes;
    private String day;
    private String exchange;
    private long maxaskdate;
    private long minaskdate;
    private long maxbiddate;
    private long minbiddate;
    private double openAsk;
    private double openBid;
    private double closeAsk;
    private double closeBid;
}
