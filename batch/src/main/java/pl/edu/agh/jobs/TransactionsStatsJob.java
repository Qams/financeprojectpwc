package pl.edu.agh.jobs;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pl.edu.agh.util.DateConvertUtil;

public class TransactionsStatsJob implements Job {

    private SparkSession sparkSession;

    private static final String HDFS_PATH = "hdfs://localhost:9000/tmp/kafka/transactions/";
    private static final String HDFS_PATH_PREFIX = "hdfs://localhost:9000/tmp/";

    public TransactionsStatsJob() {
        this.sparkSession = SparkSession
                .builder()
                .appName("Java Spark SQL batch layer transactions stats processing")
                .config("spark.master", "local")
                .getOrCreate();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            processData();
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
    }

    private void processData() throws AnalysisException {
//        String lastDay = DateConvertUtil.getLastDayDateFormat();
        String lastDay = DateConvertUtil.getTodayDateFormat();
        String today = DateConvertUtil.getTodayDateFormat();
        sparkSession.read().json(HDFS_PATH + lastDay).createTempView("tran");
        sparkSession.sql("SELECT SUM(originalAmount) as totalAmount, COUNT(*) as numberOfOperations, fromCurrency FROM tran GROUP BY fromCurrency")
                .write()
                .mode(SaveMode.Overwrite)
                .parquet(HDFS_PATH_PREFIX + "processed/transactions-by-date/outcome/" + lastDay);
        sparkSession.sql("SELECT SUM(amount) as totalAmount, COUNT(*) as numberOfOperations, toCurrency FROM tran GROUP BY toCurrency")
                .write()
                .mode(SaveMode.Overwrite)
                .parquet(HDFS_PATH_PREFIX + "processed/transactions-by-date/income/" + lastDay);
        sparkSession.catalog().dropTempView("tran");
    }
}
