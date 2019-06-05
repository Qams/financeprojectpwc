package pl.edu.agh.financeservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateShort {
    private String id;
    private String exchange;
    private Double ask;
    private Double bid;
}
