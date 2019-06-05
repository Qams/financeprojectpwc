package pl.edu.agh.financeservice.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsWindowed {
    private String currency;
    private Double incomeSumAmount;
    private Double outcomeSumAmount;
    private Long incomeTransactions;
    private Long outcomeTransactions;
    private Long dateStart;
    private Long dateEnd;
}
