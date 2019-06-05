package pl.edu.agh;

import org.apache.spark.ml.classification.*;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

public class SparkTestMain {
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL basic example")
                .config("spark.master", "local")
                .getOrCreate();

//        createCsvFile(spark);

        Dataset<Row> df_feature = spark.read()
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
        Dataset<Row> testX = training[1];

        System.out.println("TrainX size: " + trainX.count());
        System.out.println("TestX size: " + testX.count());

        RandomForestClassifier rf = new RandomForestClassifier()
                .setMaxDepth(10)
                .setNumTrees(20)
                .setFeatureSubsetStrategy("auto")
                .setSeed(9834);
        RandomForestClassificationModel model = rf.fit(trainX);
//        GBTClassifier gbtClassifier = new GBTClassifier().setMaxDepth(7).setSeed(9833);
//        GBTClassificationModel model = gbtClassifier.fit(trainX);
//        LogisticRegression logisticRegression = new LogisticRegression().setMaxIter(100);
//        LogisticRegressionModel model = logisticRegression.fit(trainX);

        Dataset<Row> predictions = model.transform(testX);
        System.out.println("CHECK 1-1: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$greater(0))).count());
        System.out.println("CHECK prediction 1 fraud 0: " + predictions.filter(predictions.col("prediction").$greater(0).and(predictions.col("isFraud").$less(1))).count());
        System.out.println("CHECK prediction 0 fraud 1: " + predictions.filter(predictions.col("isFraud").$greater(0).and(predictions.col("prediction").$less(1))).count());
        System.out.println("CHECK 0-0: " + predictions.filter(predictions.col("prediction").$less(1).and(predictions.col("isFraud").$less(1))).count());
        predictions.show(5);

//        RandomForestClassificationModel rfModel = (RandomForestClassificationModel)(model.stages()[2]);
//        System.out.println("Learned classification forest model:\n" + rfModel.toDebugString());


    }

    private static void createCsvFile(SparkSession spark) {
        Dataset<Row> df = spark.read()
                .option("mode", "DROPMALFORMED")
                .csv("src/main/resources/transactions.csv");

        df = df.withColumnRenamed("_c0", "step");
        df = df.withColumnRenamed("_c1", "type");
        df = df.withColumnRenamed("_c2", "amount");
        df = df.withColumnRenamed("_c3", "nameSender");
        df = df.withColumnRenamed("_c4", "oldBalanceSender");
        df = df.withColumnRenamed("_c5", "newBalanceSender");
        df = df.withColumnRenamed("_c6", "nameReceiver");
        df = df.withColumnRenamed("_c7", "oldBalanceReceiver");
        df = df.withColumnRenamed("_c8", "newBalanceReceiver");
        df = df.withColumnRenamed("_c9", "isFraud");
        df = df.withColumnRenamed("_c10", "isFlaggedFraud");

        Dataset<Row> df_clean = df.where(df.col("type").like("TRANSFER").or(df.col("type").like("CASH_OUT")));
//        df_clean = df_clean.where(df_clean.col("nameSender").contains("M%'"))

        df_clean = df_clean.filter(df_clean.col("nameSender").startsWith("C"));
        df_clean = df_clean.filter(df_clean.col("nameReceiver").startsWith("C"));
        df_clean = df_clean.drop("nameSender", "nameReceiver", "isFlaggedFraud", "step");

        System.out.println("Number of fraud transactions: " + df_clean.where(df_clean.col("isFraud").like("1")).count());

        df_clean = df_clean.withColumn("type", functions.regexp_replace(df_clean.col("type"), "TRANSFER", "1"));
        df_clean = df_clean.withColumn("type", functions.regexp_replace(df_clean.col("type"), "CASH_OUT", "0"));
        df_clean = df_clean.withColumn("type", df_clean.col("type").cast("Integer"));

        df_clean.show();
        System.out.println("SIZE: " + df_clean.count());

        Dataset<Row> df_feature = df_clean.where(df_clean.col("isFraud").like("0").or(df_clean.col("isFraud").like("1")));
        df_feature = df_feature.withColumn("errorBalanceSender", df_feature.col("newBalanceSender").plus(df_feature.col("amount")).minus(df_feature.col("oldBalanceSender")));
        df_feature = df_feature.withColumn("errorBalanceReceiver", df_feature.col("oldBalanceReceiver").plus(df_feature.col("amount")).minus(df_feature.col("newBalanceReceiver")));
        df_feature = df_feature.drop("oldBalanceSender", "oldBalanceReceiver");
        df_feature = df_feature.withColumnRenamed("newBalanceSender", "balanceSender");
        df_feature = df_feature.withColumnRenamed("newBalanceReceiver", "balanceReceiver");
        df_feature = df_feature.withColumn("noErrors", functions.when(df_feature.col("errorBalanceSender").equalTo(df_feature.col("errorBalanceReceiver")).and(df_feature.col("errorBalanceSender").$greater$eq(0)).and(df_feature.col("errorBalanceSender").$less$eq(0)), 1).otherwise(0));

        df_feature.show();
        System.out.println("No errors unexpected positive: " + df_feature.where(df_feature.col("errorBalanceSender").equalTo(df_feature.col("errorBalanceReceiver")).and(df_feature.col("errorBalanceSender").$greater(0)).and(df_feature.col("noErrors").$greater(0))).count());
        System.out.println("No errors unexpected negative: " + df_feature.where(df_feature.col("errorBalanceSender").equalTo(df_feature.col("errorBalanceReceiver")).and(df_feature.col("errorBalanceSender").$less(0)).and(df_feature.col("noErrors").$greater(0))).count());
        System.out.println("No errors expected: " + df_feature.where(df_feature.col("errorBalanceSender").equalTo(df_feature.col("errorBalanceReceiver")).and(df_feature.col("errorBalanceSender").$less(0)).and(df_feature.col("noErrors").$greater$eq(0)).and(df_feature.col("noErrors").$less$eq(0))).count());
        df_feature.write().csv("src/main/resources/newone4.csv");
    }

}
