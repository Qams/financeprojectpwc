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
public class FinTransaction {
    private String ftowner;
    private Double transactionAmount;
    private String id;
    private String sender;
    private String receiver;
    private Date timestamp;
    private String title;
    private String fromAccount;
    private String toAccount;
    private Double originalAmount;
    private Double amount;
    private String fromCurrency;
    private String toCurrency;
    private String type;
}
