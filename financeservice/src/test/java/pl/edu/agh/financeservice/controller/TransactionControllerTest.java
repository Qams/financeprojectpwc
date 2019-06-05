package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import pl.edu.agh.financeservice.model.transactions.*;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.service.transactions.TransactionService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Test
    public void shouldFindTotalBalanceForAccount() {
        // given
        AppUser appUser = new AppUser();
        String accountNumber = "123";
        when(transactionService.calculateAccountBalance(accountNumber)).thenReturn(BigDecimal.TEN);

        // when
        TotalAccountBalance result = transactionController.findTotalBalanceForAccount(appUser, accountNumber);

        // then
        assertEquals(result.getTotalBalance(), BigDecimal.TEN);
    }

    @Test
    public void shouldFindTransactionForAccount() {
        // given
        String account = "123";
        List<FinTransaction> finTransactionList = Collections.singletonList(
                new FinTransaction("ftowner", new Date(), BigDecimal.ONE, "12", "EUR", BigDecimal.TEN, "1", BigDecimal.TEN, "23", "45", "hey", "84", "USD", "Transfer")
        );
        when(transactionService.findTransactionsForAccount(account)).thenReturn(finTransactionList);

        // when
        List<FinTransaction> result = transactionController.findTransactionForAccount(account);

        // then
        assertEquals(result, finTransactionList);
        assertEquals(result.get(0).getAmount(), finTransactionList.get(0).getAmount());
        assertEquals(result.get(0).getFromaccount(), finTransactionList.get(0).getFromaccount());
        assertEquals(result.get(0).getFromcurrency(), finTransactionList.get(0).getFromcurrency());
        assertEquals(result.get(0).getFtamount(), finTransactionList.get(0).getFtamount());
        assertEquals(result.get(0).getFtowner(), finTransactionList.get(0).getFtowner());
        assertEquals(result.get(0).getFttimestamp(), finTransactionList.get(0).getFttimestamp());
        assertEquals(result.get(0).getId(), finTransactionList.get(0).getId());
        assertEquals(result.get(0).getOriginalamount(), finTransactionList.get(0).getOriginalamount());
        assertEquals(result.get(0).getReceiver(), finTransactionList.get(0).getReceiver());
        assertEquals(result.get(0).getSender(), finTransactionList.get(0).getSender());
        assertEquals(result.get(0).getTitle(), finTransactionList.get(0).getTitle());
        assertEquals(result.get(0).getToaccount(), finTransactionList.get(0).getToaccount());
        assertEquals(result.get(0).getType(), finTransactionList.get(0).getType());
    }

    @Test
    public void shouldFindStatsForTransactions() {
        assertTrue(true);
    }

    @Test
    public void shouldFindTransactionsIncomeStatsForDate() {
        // given
        String date = "09-03-2019";
        List<TransactionsIncomeStats> stats = Collections.singletonList(
                new TransactionsIncomeStats("EUR", 2L, 10.0)
        );
        when(transactionService.getTransactionStatsIncomeForDate(date)).thenReturn(stats);

        // when
        List<TransactionsIncomeStats> result = transactionController.findTransactionsIncomeStatsForDate(date);

        // then
        assertEquals(result.get(0).getNumberOfOperations(), stats.get(0).getNumberOfOperations());
        assertEquals(result.get(0).getToCurrency(), stats.get(0).getToCurrency());
        assertEquals(result.get(0).getTotalAmount(), stats.get(0).getTotalAmount(), 1.0);
    }

    @Test
    public void shouldFindTransactionsOutcomeStatsForDate() {
        // given
        String date = "09-03-2019";
        List<TransactionsOutcomeStats> stats = Collections.singletonList(
                new TransactionsOutcomeStats("EUR", 2L, 10.0)
        );
        when(transactionService.getTransactionStatsOutcomeForDate(date)).thenReturn(stats);

        // when
        List<TransactionsOutcomeStats> result = transactionController.findTransactionsOutomeStatsForDate(date);

        // then
        assertEquals(result.get(0).getNumberOfOperations(), stats.get(0).getNumberOfOperations());
        assertEquals(result.get(0).getFromCurrency(), stats.get(0).getFromCurrency());
        assertEquals(result.get(0).getTotalAmount(), stats.get(0).getTotalAmount(), 1.0);
    }

    @Test
    public void shouldTransferMoney() throws Exception {
        // given
        AppUser appUser = new AppUser();
        TransferMoneyModel transferMoneyModel = new TransferMoneyModel("123", "345", "EUR", 1.5D, "234", "567", "title");

        // when
        ResponseEntity result = transactionController.transferMoney(transferMoneyModel, appUser);

        // then
        assertEquals(result.getStatusCode(), ResponseEntity.ok().build().getStatusCode());
    }

    @Test
    public void shouldTransferMoneyReturn404() throws Exception {
        // given
        AppUser appUser = new AppUser();
        TransferMoneyModel transferMoneyModel = new TransferMoneyModel("123", "345", "EUR", 1.5D, "234", "567", "title");
        doThrow(Exception.class).when(transactionService).transferMoney(transferMoneyModel, appUser);

        // when
        ResponseEntity result = transactionController.transferMoney(transferMoneyModel, appUser);

        // then
        assertEquals(result.getStatusCode(), ResponseEntity.badRequest().build().getStatusCode());
    }
}