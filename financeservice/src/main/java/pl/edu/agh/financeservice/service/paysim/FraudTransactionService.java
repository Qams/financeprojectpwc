package pl.edu.agh.financeservice.service.paysim;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.financeservice.model.FraudPaysimTransaction;
import scala.collection.mutable.WrappedArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FraudTransactionService {

    @Autowired
    private SparkSession sparkSession;

    public List<FraudPaysimTransaction> findFraudTransactions() {
        List<Row> probability = sparkSession.read()
                .parquet("hdfs://localhost:9000/tmp/processed/paysim/fraud").select("probability").collectAsList();
        List<Row> features = sparkSession.read()
                .parquet("hdfs://localhost:9000/tmp/processed/paysim/fraud").select("features").collectAsList();


        List<FraudPaysimTransaction> fraudPaysimTransactions = new ArrayList<>();
        for(Row row: features) {
            GenericRowWithSchema objects = (GenericRowWithSchema) row.get(0);
            WrappedArray<Double> wrappedArray$ = (WrappedArray<Double>) objects.get(3);
            fraudPaysimTransactions.add(new FraudPaysimTransaction(UUID.randomUUID().toString(),
                    BigDecimal.valueOf(wrappedArray$.apply(1)), 0));
        }

        int i = 0;
        for(Row row: probability) {
            GenericRowWithSchema objects = (GenericRowWithSchema) row.get(0);
            WrappedArray<Double> wrappedArray$ = (WrappedArray<Double>) objects.get(3);
            fraudPaysimTransactions.get(i).setProbability(wrappedArray$.apply(1));
            i++;
        }
        return fraudPaysimTransactions;
    }

}
