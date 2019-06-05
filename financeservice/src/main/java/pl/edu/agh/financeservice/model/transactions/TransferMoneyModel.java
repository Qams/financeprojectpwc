package pl.edu.agh.financeservice.model.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyModel {
    @NotNull
    private String fromAccount;
    @NotNull
    private String toAccount;
    @NotNull
    private String currency;
    @NotNull
    private Double amount;
    @NotNull
    private String sender;
    @NotNull
    private String receiver;
    @NotNull
    private String title;
}
