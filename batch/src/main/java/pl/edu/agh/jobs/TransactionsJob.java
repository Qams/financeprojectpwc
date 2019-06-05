package pl.edu.agh.jobs;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TransactionsJob implements Job {

    private SparkSession sparkSession;

    private static final String HDFS_PATH = "hdfs://localhost:9000/tmp/kafka/transactions/";
    private static final String HDFS_PROCESSED_PATH = "hdfs://localhost:9000/tmp/processed/transactions/";

    public TransactionsJob() {
        this.sparkSession = SparkSession
                .builder()
                .appName("Java Spark SQL batch layer transactions processing")
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
        sparkSession.read().json(HDFS_PATH + "*").createTempView("tran");
        sparkSession.sql("SELECT toAccount, SUM(amount) as zy, MAX(timestamp) as lastprofittime FROM tran GROUP BY toAccount")
                .createTempView("profit");
        sparkSession.sql("SELECT fromAccount, SUM(originalAmount) as str, MAX(timestamp) as lastlosstime FROM tran GROUP BY fromAccount")
                .createTempView("loss");
        sparkSession.sql("SELECT *, (COALESCE(zy, 0) - COALESCE(str,0)) as balance FROM profit FULL OUTER JOIN loss ON profit.toAccount = loss.fromAccount")
                .write()
                .mode(SaveMode.Overwrite)
                .parquet(HDFS_PROCESSED_PATH + "processed-transactions");
        sparkSession.catalog().dropTempView("tran");
        sparkSession.catalog().dropTempView("profit");
        sparkSession.catalog().dropTempView("loss");
    }
}
