package pl.edu.agh.financeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.financeservice.model.transactions.*;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.service.transactions.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/balance")
    public TotalAccountBalance findTotalBalanceForAccount(@AuthenticationPrincipal AppUser user,
                                                          @RequestParam("accountNumber") String accountNumber) {

        // TODO SPRING SECURITY TO FETCH ONLY ACCOUNT RELATED TO USER
        return TotalAccountBalance.builder()
                .totalBalance(transactionService.calculateAccountBalance(accountNumber))
                .build();
    }

    @GetMapping(value = "")
    public List<FinTransaction> findTransactionForAccount(@RequestParam("accountNumber") String accountNumber) {
        return transactionService.findTransactionsForAccount(accountNumber);
    }

//    // TODO stats for transactions (batch processed)
//    @GetMapping(value = "/stats")
//    public void findStatsForTransactions() {
//
//    }

    @PostMapping(value = "")
    public ResponseEntity transferMoney(@Valid @RequestBody TransferMoneyModel transferMoneyModel,
                                        @AuthenticationPrincipal AppUser appUser) {
        try {
            transactionService.transferMoney(transferMoneyModel, appUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/stats/income/{date}")
    public List<TransactionsIncomeStats> findTransactionsIncomeStatsForDate(@PathVariable("date") String date) {
        return transactionService.getTransactionStatsIncomeForDate(date);
    }

    @GetMapping(value = "/stats/outcome/{date}")
    public List<TransactionsOutcomeStats> findTransactionsOutomeStatsForDate(@PathVariable("date") String date) {
        return transactionService.getTransactionStatsOutcomeForDate(date);
    }
}
