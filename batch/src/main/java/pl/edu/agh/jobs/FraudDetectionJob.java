package pl.edu.agh.jobs;

import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class FraudDetectionJob implements Job {

    private SparkSession sparkSession;

    private RandomForestClassificationModel model;

    private Dataset<Row> testX;

    private Dataset<Row>[] splitTest;

    int weightsElement = 0;

    public FraudDetectionJob() {
        sparkSession = SparkSession
                .builder()
                .appName("Java Spark SQL basic example")
                .config("spark.master", "local")
                .getOrCreate();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if(model == null) {
            prepareModel();
        }
        double[] weigths = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        splitTest = testX.randomSplit(weigths);
        Dataset<Row> predictions = model.transform(splitTest[weightsElement]);

        System.out.println("CHECK 1-1: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$greater(0))).count());
        System.out.println("CHECK prediction 1 fraud 0: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$less(1))).count());
        System.out.println("CHECK prediction 0 fraud 1: " + predictions.filter(predictions.col("isFraud").$greater(0).and(predictions.col("prediction").$less(1))).count());
        System.out.println("CHECK 0-0: "+ predictions.filter(predictions.col("prediction").$less(1).and(predictions.col("isFraud").$less(1))).count());
        predictions.show(5);


        weightsElement++;
    }

    public void runJob() {
        if(model == null) {
            prepareModel();
        }
        double[] weigths = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        splitTest = testX.randomSplit(weigths);
        for(int i=0; i<20; i++) {
            Dataset<Row> predictions = model.transform(splitTest[i]);

            System.out.println("CHECK 1-1: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$greater(0))).count());
            System.out.println("CHECK prediction 1 fraud 0: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$less(1))).count());
            System.out.println("CHECK prediction 0 fraud 1: " + predictions.filter(predictions.col("isFraud").$greater(0).and(predictions.col("prediction").$less(1))).count());
            System.out.println("CHECK 0-0: " + predictions.filter(predictions.col("prediction").$less(1).and(predictions.col("isFraud").$less(1))).count());
            predictions.show(5);
            predictions.filter(predictions.col("prediction").$greater(0))
                    .write().mode(SaveMode.Append).parquet( "hdfs://localhost:9000/tmp/processed/paysim/fraud");
            System.out.println("GO SLEEP");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void prepareModel() {
        Dataset<Row> df_feature = sparkSession.read()
                .option("mode", "DROPMALFORMED")
                .csv("batch/src/main/resources/newone4.csv");

        df_feature = df_feature.withColumnRenamed("_c0", "type");
        df_feature = df_feature.withColumnRenamed("_c1", "amount");
        df_feature = df_feature.withColumnRenamed("_c2", "balanceSender");
        df_feature = df_feature.withColumnRenamed("_c3", "balanceReceiver");
        df_feature = df_feature.withColumnRenamed("_c4", "isFraud");
        df_feature = df_feature.withColumnRenamed("_c5", "errorBalanceSender");
        df_feature = df_feature.withColumnRenamed("_c6", "errorBalanceReceiver");
        df_feature = df_feature.withColumnRenamed("_c7", "noErrors");

        df_feature = df_feature.withColumn("type", df_feature.col("type").cast("Double"));
        df_feature = df_feature.withColumn("amount", df_feature.col("amount").cast("Double"));
        df_feature = df_feature.withColumn("balanceSender", df_feature.col("balanceSender").cast("Double"));
        df_feature = df_feature.withColumn("balanceReceiver", df_feature.col("balanceReceiver").cast("Double"));
        df_feature = df_feature.withColumn("isFraud", df_feature.col("isFraud").cast("Double"));
        df_feature = df_feature.withColumn("errorBalanceSender", df_feature.col("type").cast("Double"));
        df_feature = df_feature.withColumn("errorBalanceReceiver", df_feature.col("errorBalanceReceiver").cast("Double"));
        df_feature = df_feature.withColumn("noErrors", df_feature.col("noErrors").cast("Double"));

        // COMMENT SECTION
        String[] inputCols = {"type", "amount", "balanceSender", "balanceReceiver", "errorBalanceSender", "errorBalanceReceiver", "noErrors"};

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(inputCols)
                .setOutputCol("features");

        Dataset<Row> newDf = assembler.transform(df_feature);
        StringIndexer stringIndexer = new StringIndexer()
                .setInputCol("isFraud")
                .setOutputCol("label");
        Dataset<Row> df4 = stringIndexer.fit(newDf).transform(newDf);
//
        // earlier df_feature
        double[] weights = {0.8, 0.2};
        Dataset<Row>[] training;
        df4 = df4.drop("type", "amount", "balanceSender", "balanceReceiver", "errorBalanceSender", "errorBalanceReceiver");
        training = df4.randomSplit(weights);
        Dataset<Row> trainX = training[0];
        testX = training[1];

        System.out.println("TrainX size: " + trainX.count());
        System.out.println("TestX size: " + testX.count());

        RandomForestClassifier rf = new RandomForestClassifier()
                .setMaxDepth(10)
                .setNumTrees(20)
                .setFeatureSubsetStrategy("auto")
                .setSeed(9834);
        model = rf.fit(trainX);
    }

    private void processData() {

    }
}
