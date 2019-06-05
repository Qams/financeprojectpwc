package pl.edu.agh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinTransactionWindowed {

    private String currency;
    private Double incomeSumAmount;
    private Double outcomeSumAmount;
    private Long incomeTransactions;
    private Long outcomeTransactions;
    private Long dateStart;
    private Long dateEnd;
}
