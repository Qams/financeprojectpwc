package pl.edu.agh.jobs;

import org.apache.spark.sql.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pl.edu.agh.model.AskBid;
import pl.edu.agh.util.DateConvertUtil;

import static org.apache.spark.sql.functions.*;

public class ExchangeRateJob implements Job {

    private SparkSession sparkSession;

    private static final String HDFS_PATH_EXCHANGE_RATE = "hdfs://localhost:9000/tmp/kafka/exchangeRate/";
    private static final String HDFS_PATH_EXCHANGE_STATS = "hdfs://localhost:9000/tmp/processed/exchangeRate/%s-exchange-stats";

    public ExchangeRateJob() {
        this.sparkSession = SparkSession
                .builder()
                .appName("Java Spark SQL batch layer")
                .config("spark.master", "local")
                .getOrCreate();
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String lastDay = DateConvertUtil.getTodayDateFormat();
        String today = DateConvertUtil.getTodayDateFormat();
        System.out.println(lastDay);
        Dataset<Row> dataset = sparkSession.read().json(HDFS_PATH_EXCHANGE_RATE + lastDay);
        createExchangeRateStats(dataset, lastDay)
                .write()
                .mode(SaveMode.Overwrite)
                .parquet(String.format(HDFS_PATH_EXCHANGE_STATS, lastDay));
    }

    private Dataset<Row> createExchangeRateStats(Dataset<Row> dataset, String date) {
        return createResultsForExchange("EUR", "USD", date, dataset)
            .union(createResultsForExchange("EUR", "GBP", date, dataset))
            .union(createResultsForExchange("EUR", "CHF", date, dataset))
            .union(createResultsForExchange("GBP", "CHF", date, dataset))
            .union(createResultsForExchange("GBP", "USD", date, dataset))
            .union(createResultsForExchange("USD", "CHF", date, dataset));
    }

    private Dataset<Row> createResultsForExchange(String from, String to, String date, Dataset<Row> rowDataset) {
        Dataset<Row> filteredDataset = rowDataset.filter((rowDataset
                .col("from")
                .$eq$eq$eq(from)
                .and(rowDataset.col("to")
                        .$eq$eq$eq(to))));
        filteredDataset.registerTempTable("exchangerate");
        Dataset<Row> dataset1 = createBasicExchangeRateDataset(from, to, date);
        return appendStatsColumns(from, to, date, dataset1);
    }

    private Dataset<Row> appendStatsColumns(String from, String to, String date, Dataset<Row> dataset1) {
        Dataset<Row> dataset = dataset1.limit(1);
        dataset = dataset.withColumn("day", lit(date));
        dataset = dataset.withColumn("exchange", lit(from + to));
        dataset = dataset.withColumn("maxaskdate", lit(findMaxAskTimestamp()));
        dataset = dataset.withColumn("minaskdate", lit(findMinAskTimestamp()));
        dataset = dataset.withColumn("maxbiddate", lit(findMaxBidTimestamp()));
        dataset = dataset.withColumn("minbiddate", lit(findMinBidTimestamp()));
        AskBid askBidOpen = findOpenExchangeRate();
        AskBid askBidClose = findCloseExchangeRate();
        dataset = dataset.withColumn("openAsk", lit(askBidOpen.getAsk()));
        dataset = dataset.withColumn("openBid", lit(askBidOpen.getBid()));
        dataset = dataset.withColumn("closeAsk", lit(askBidClose.getAsk()));
        dataset = dataset.withColumn("closeBid", lit(askBidClose.getBid()));
        return dataset;
    }

    private Dataset<Row> createBasicExchangeRateDataset(String from, String to, String date) {
        Dataset<Row> dataset = sparkSession.read().json(HDFS_PATH_EXCHANGE_RATE + date);
        return dataset
                .filter(dataset.col("from").$eq$eq$eq(from))
                .filter(dataset.col("to").$eq$eq$eq(to))
                .agg(min("bid").as("minbid"), max("bid").as("maxbid"), min("ask").as("minask"),
                        max("ask").as("maxask"), count("ask").as("changes"))
                .toDF();
    }

    private Long findMaxAskTimestamp() {
        Dataset<Row> dataset = sparkSession.sql("SELECT timestamp, ask FROM exchangerate WHERE ask = (SELECT max(ask) FROM exchangerate)");
        return dataset.first().getLong(0);
    }

    private Long findMinAskTimestamp() {
        Dataset<Row> dataset = sparkSession.sql("SELECT timestamp, ask FROM exchangerate WHERE ask = (SELECT min(ask) FROM exchangerate)");
        return dataset.first().getLong(0);
    }

    private Long findMaxBidTimestamp() {
        Dataset<Row> dataset = sparkSession.sql("SELECT timestamp, bid FROM exchangerate WHERE bid = (SELECT max(bid) FROM exchangerate)");
        return dataset.first().getLong(0);
    }

    private Long findMinBidTimestamp() {
        Dataset<Row> dataset = sparkSession.sql("SELECT timestamp, bid FROM exchangerate WHERE bid = (SELECT min(bid) FROM exchangerate)");
        return dataset.first().getLong(0);
    }

    private AskBid findOpenExchangeRate() {
        Dataset<Row> dataset = sparkSession.sql("SELECT ask, bid FROM exchangerate WHERE timestamp = (SELECT min(timestamp) FROM exchangerate)");
        AskBid askBid = new AskBid();
        askBid.setAsk(dataset.first().getDouble(0));
        askBid.setBid(dataset.first().getDouble(1));
        return askBid;
    }

    private AskBid findCloseExchangeRate() {
        Dataset<Row> dataset = sparkSession.sql("SELECT ask, bid FROM exchangerate WHERE timestamp = (SELECT max(timestamp) FROM exchangerate)");
        AskBid askBid = new AskBid();
        askBid.setAsk(dataset.first().getDouble(0));
        askBid.setBid(dataset.first().getDouble(1));
        return askBid;
    }

}
