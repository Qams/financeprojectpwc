package pl.edu.agh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateWindowStats {
    private Date beginTimestamp;
    private Date endTimestamp;
    private String exchangeRate;
    private Long changes;
    private Long minBid;
    private Long maxBid;
    private Long minAsk;
    private Long maxAsk;


}
