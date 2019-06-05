package pl.edu.agh.financeservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class ExchangeRateShortDate {
    private String exchange;
    private Double ask;
    private Double bid;
    private Long timestamp;
}
