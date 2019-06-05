package pl.edu.agh.financeservice.service.transactions;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.financeservice.config.ProducerConfig;
import pl.edu.agh.financeservice.model.transactions.TransferTransaction;

public class TransactionProducerService {
    private KafkaProducer<String, TransferTransaction> producer = new ProducerConfig().finTransactionProducer();

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferTransaction.class);

    public boolean dispatch(TransferTransaction finTransaction) {
        ProducerRecord<String, TransferTransaction> record =
                new ProducerRecord<>("fin-transaction", finTransaction.getId(), finTransaction);
        try {
            this.producer.send(record);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}