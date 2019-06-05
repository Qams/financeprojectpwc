package pl.edu.agh.financeservice.repository.hdfs;

import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.config.PathConfig;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShortDate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateStats;

import java.util.List;

import static org.apache.spark.sql.functions.count;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.min;

@Repository
public class ExchangeRateHdfsRepository {

    @Autowired
    private SparkSession sparkSession;

    public Dataset<Row> getExchangeRateDayStats(String date, String from, String to) {
        Dataset<Row> dataset = sparkSession.read().json(PathConfig.HDFS_PREFIX + "/tmp/kafka/exchangeRate/" + date);
        return dataset
                .filter(dataset.col("from").$eq$eq$eq(from))
                .filter(dataset.col("to").$eq$eq$eq(to))
                .agg(min("bid"), max("bid"), min("ask"), max("ask"), count("ask"));
    }

    public List<ExchangeRateShortDate> getExchangeRateDayRates(String date, String from, String to) throws AnalysisException {
        Encoder<ExchangeRateShortDate> exchageRateEncoder = Encoders.bean(ExchangeRateShortDate.class);
        sparkSession.read().json(PathConfig.HDFS_PREFIX + "/tmp/kafka/exchangeRate/" + date).createTempView("course");
        String exchange = from + to;
        Dataset<ExchangeRateShortDate> as = sparkSession.sql(
                String.format("SELECT '%s' as exchange, min(timestamp) as timestamp, max(bid) as bid, max(ask) as ask FROM course WHERE from = '%s' AND to = '%s' AND (SELECT min(timestamp) FROM course  WHERE from = '%s' AND to = '%s') = timestamp", exchange, from, to, from, to))
                .as(exchageRateEncoder);
        sparkSession.catalog().dropTempView("course");
        return as.collectAsList();
    }

    public List<ExchangeRateStats> getAdvanceExchangeRateDayStats(String date) {
        Encoder<ExchangeRateStats> exchageRateEncoder = Encoders.bean(ExchangeRateStats.class);
        return sparkSession.read()
                .parquet(String.format(PathConfig.HDFS_PREFIX + "/tmp/processed/exchangeRate/%s-exchange-stats", date))
                .as(exchageRateEncoder)
                .collectAsList();
    }
}
