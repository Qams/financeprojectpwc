package pl.edu.agh.financeservice.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferTransaction {
    private String id;
    private String sender;
    private String receiver;
    private Date timestamp;
    private String title;
    private String fromAccount;
    private String toAccount;
    private BigDecimal originalAmount;
    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;
    private String type;
}
