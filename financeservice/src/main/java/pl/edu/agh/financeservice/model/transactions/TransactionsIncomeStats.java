package pl.edu.agh.financeservice.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionsIncomeStats {
    private String toCurrency;
    private long numberOfOperations;
    private double totalAmount;
}
