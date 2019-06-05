package pl.edu.agh.financeservice.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import pl.edu.agh.financeservice.model.transactions.TransferTransaction;
import pl.edu.agh.financeservice.serializer.JsonSerializer;

import java.util.Properties;

public class ProducerConfig {

    public KafkaProducer<String, TransferTransaction> finTransactionProducer() {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        KafkaProducer<String, TransferTransaction> producer = new KafkaProducer<>(kafkaProps, stringKeySerializer(), transactionJsonSerializer());
        return producer;
    }

    private Serializer stringKeySerializer() {
        return new StringSerializer();
    }

    private Serializer transactionJsonSerializer() {
        return new JsonSerializer();
    }
}