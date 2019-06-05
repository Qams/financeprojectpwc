package pl.edu.agh.financeservice.repository.hdfs;

import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.config.PathConfig;
import pl.edu.agh.financeservice.model.transactions.AccountBalanceCalculated;
import pl.edu.agh.financeservice.model.transactions.TransactionsIncomeStats;
import pl.edu.agh.financeservice.model.transactions.TransactionsOutcomeStats;

import java.math.BigDecimal;
import java.util.Date;

import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.sum;

@Repository
public class TransactionHdfsRepository {

    @Autowired
    private SparkSession sparkSession;

    public AccountBalanceCalculated getTransactionBalance(String account) {
        Dataset<Row> dataset = sparkSession
                .read().parquet(PathConfig.HDFS_PREFIX + "/tmp/processed/transactions/processed-transactions");
        dataset.show();
        Row row = dataset.filter(dataset.col("fromAccount")
                .$eq$eq$eq(account)
                .or(dataset
                        .col("toAccount")
                        .$eq$eq$eq(account)))
                .agg(sum("balance"), max(dataset.col("lastlosstime")), max(dataset.col("lastprofittime")))
                .first();
        AccountBalanceCalculated balanceCalculated = new AccountBalanceCalculated();
        if(row != null) {
            if(row.get(0) != null) {
                balanceCalculated.setAmount(new BigDecimal(row.getDouble(0)).setScale(5, BigDecimal.ROUND_DOWN));
            }
            else {
                balanceCalculated.setAmount(BigDecimal.ZERO);
            }
            if(row.get(1) != null) {
                balanceCalculated.setLastLossDate(new Date(row.getLong(1)));
            } else {
                balanceCalculated.setLastLossDate(new Date(1));
            }
            if(row.get(2) != null) {
                balanceCalculated.setLastProfitDate(new Date(row.getLong(2)));
            }
            else {
                balanceCalculated.setLastProfitDate(new Date(1));
            }
        }
        return balanceCalculated;
    }

    public Dataset<TransactionsIncomeStats> getTransactionIncomeStats(String date) {
        Encoder<TransactionsIncomeStats> transactionsStatsEncoder = Encoders.bean(TransactionsIncomeStats.class);
        return sparkSession.read()
                .parquet(PathConfig.HDFS_PREFIX + "/tmp/processed/transactions-by-date/income/" + date)
                .as(transactionsStatsEncoder);
    }

    public Dataset<TransactionsOutcomeStats> getTransactionOutcomeStats(String date) {
        Encoder<TransactionsOutcomeStats> transactionsStatsEncoder = Encoders.bean(TransactionsOutcomeStats.class);
        return sparkSession.read()
                .parquet(PathConfig.HDFS_PREFIX + "/tmp/processed/transactions-by-date/outcome/" + date)
                .as(transactionsStatsEncoder);
    }
}
