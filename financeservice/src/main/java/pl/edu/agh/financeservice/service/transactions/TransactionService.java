package pl.edu.agh.financeservice.service.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.financeservice.model.exchange.ExchangeRate;
import pl.edu.agh.financeservice.model.transactions.*;
import pl.edu.agh.financeservice.model.user.postgres.Account;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.repository.cassandra.TransactionRepository;
import pl.edu.agh.financeservice.repository.hdfs.TransactionHdfsRepository;
import pl.edu.agh.financeservice.repository.postgres.AccountRepository;
import pl.edu.agh.financeservice.service.exchange.ExchangeRateService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionHdfsRepository transactionHdfsRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    private TransactionProducerService producerService = new TransactionProducerService();

    public BigDecimal calculateAccountBalance(String account) {
        AccountBalanceCalculated accountBalanceCalculated = transactionHdfsRepository.getTransactionBalance(account);
        BigDecimal lastBalance = BigDecimal.ZERO;
        if(accountBalanceCalculated != null) {
            Date lossDate = accountBalanceCalculated.getLastLossDate();
            Date profitDate = accountBalanceCalculated.getLastProfitDate();
            if(lossDate != null && profitDate != null && lossDate.after(profitDate)) {
                lastBalance = transactionRepository.findAccountBalanceForOwnerAfterDate(account, lossDate.getTime());
            }
            else if(lossDate != null && profitDate != null && lossDate.before(profitDate)) {
                lastBalance = transactionRepository.findAccountBalanceForOwnerAfterDate(account, profitDate.getTime());
            }
            else {
                lastBalance = transactionRepository.findAccountBalanceForOwnerAfterDate(account, 0L);
            }
        }
        if(accountBalanceCalculated != null && accountBalanceCalculated.getAmount() != null) {
            System.out.println("Last balance: " + lastBalance);
            System.out.println("Hdfs calculation:  " + accountBalanceCalculated.getAmount());
            return lastBalance.add(accountBalanceCalculated.getAmount());
        }
        return BigDecimal.ZERO;
    }

    public List<FinTransaction> findTransactionsForAccount(String accountNumber) {
        return transactionRepository.findAll(accountNumber);
    }

    public void transferMoney(TransferMoneyModel transferMoneyModel, AppUser appUser) throws Exception {
        Account fromAccount = accountRepository.findAccountByNumber(transferMoneyModel.getFromAccount());
        Account toAccount = accountRepository.findAccountByNumber(transferMoneyModel.getToAccount());
        BigDecimal exchange1 = BigDecimal.ONE;
        BigDecimal exchange2 = BigDecimal.ONE;
        String toCurrency = transferMoneyModel.getCurrency();
        String currency = transferMoneyModel.getCurrency();
        if(fromAccount.getAppUser().getId() != appUser.getId()) {
            throw new Exception();
        }
        if(fromAccount != null && !fromAccount.getCurrency().toString().equals(currency)) {
            ExchangeRate exchangeRate = exchangeRateService.findCurrentExchangeRate(fromAccount.getCurrency()+currency);
            if(exchangeRate != null) {
                exchange1 = BigDecimal.ONE.divide(exchangeRate.getAsk(), 5, BigDecimal.ROUND_HALF_UP);
            }
            else {
                exchangeRate = exchangeRateService.findCurrentExchangeRate(currency+fromAccount.getCurrency());
                if(exchangeRate != null) {
                    exchange1 = exchangeRate.getAsk();
                }
                else {
                    throw new Exception();
                }
            }
        }
        if(toAccount != null &&!toAccount.getCurrency().toString().equals(currency)) {
            toCurrency = toAccount.getCurrency().toString();
            ExchangeRate exchangeRate = exchangeRateService.findCurrentExchangeRate(currency+toAccount.getCurrency());
            if(exchangeRate != null) {
                exchange2 = exchangeRate.getAsk();
            }
            else {
                exchangeRate = exchangeRateService.findCurrentExchangeRate(toAccount.getCurrency()+currency);
                if(exchangeRate != null) {
                    exchange2 = BigDecimal.ONE.divide(exchangeRate.getAsk(), 5, BigDecimal.ROUND_HALF_UP);
                }
                else {
                    throw new Exception();
                }
            }
        }
        System.out.println("EXCHANGE 1: " + exchange1);
        System.out.println("EXCHANGE 2: " + exchange2);
        BigDecimal exchangeAmount1 = exchange1
                .multiply(new BigDecimal(transferMoneyModel.getAmount()).setScale(5, BigDecimal.ROUND_HALF_UP))
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal exchangeAmount2 = exchangeAmount1
                .multiply(exchange2)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .id(UUID.randomUUID().toString())
                .sender(appUser.getFirstName() + " " + appUser.getSurname())
                .receiver(transferMoneyModel.getReceiver())
                .timestamp(new Date())
                .title(transferMoneyModel.getTitle())
                .fromAccount(fromAccount.getNumber())
                .toAccount(transferMoneyModel.getToAccount())
                .originalAmount(exchangeAmount1)
                .amount(exchangeAmount2)
                .fromCurrency(fromAccount.getCurrency().toString())
                .toCurrency(toCurrency)
                .type("TRANSFER")
                .build();
        System.out.println(transferTransaction);
        producerService.dispatch(transferTransaction);
    }

    public List<TransactionsIncomeStats> getTransactionStatsIncomeForDate(String date) {
        return transactionHdfsRepository.getTransactionIncomeStats(date).collectAsList();
    }

    public List<TransactionsOutcomeStats> getTransactionStatsOutcomeForDate(String date) {
        return transactionHdfsRepository.getTransactionOutcomeStats(date).collectAsList();
    }
}
