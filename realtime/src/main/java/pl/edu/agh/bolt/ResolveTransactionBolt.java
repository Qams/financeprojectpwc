package pl.edu.agh.bolt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import pl.edu.agh.model.FinTransaction;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class ResolveTransactionBolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        tuple.getValues().forEach(
                val -> {
                    String json = (String) val;
                    try {
                        JsonNode rootNode = mapper.readTree(json);
                        FinTransaction ft = FinTransaction.builder()
                                .id(rootNode.get("id").asText())
                                .ftowner(rootNode.get("toAccount").asText())
                                .transactionAmount(rootNode.get("amount").asDouble())
                                .timestamp(new Date(rootNode.get("timestamp").asLong()))
                                .sender(rootNode.get("sender").asText())
                                .receiver(rootNode.get("receiver").asText())
                                .title(rootNode.get("title").asText())
                                .fromAccount(rootNode.get("fromAccount").asText())
                                .toAccount(rootNode.get("toAccount").asText())
                                .originalAmount(rootNode.get("originalAmount").asDouble())
                                .amount(rootNode.get("amount").asDouble())
                                .fromCurrency(rootNode.get("fromCurrency").asText())
                                .toCurrency(rootNode.get("toCurrency").asText())
                                .type(rootNode.get("type").asText())
                                .build();
                        FinTransaction ft2 = FinTransaction.builder()
                                .id(rootNode.get("id").asText())
                                .ftowner(rootNode.get("fromAccount").asText())
                                .transactionAmount(-rootNode.get("originalAmount").asDouble())
                                .timestamp(new Date(rootNode.get("timestamp").asLong()))
                                .sender(rootNode.get("sender").asText())
                                .receiver(rootNode.get("receiver").asText())
                                .title(rootNode.get("title").asText())
                                .fromAccount(rootNode.get("fromAccount").asText())
                                .toAccount(rootNode.get("toAccount").asText())
                                .originalAmount(rootNode.get("originalAmount").asDouble())
                                .amount(-rootNode.get("amount").asDouble())
                                .fromCurrency(rootNode.get("fromCurrency").asText())
                                .toCurrency(rootNode.get("toCurrency").asText())
                                .type(rootNode.get("type").asText())
                                .build();
                        outputCollector.emit(new Values(ft.getFtowner(), ft.getTransactionAmount(), ft.getId(),
                                ft.getTimestamp().getTime(), ft.getSender(), ft.getReceiver(), ft.getTitle(), ft.getFromAccount(),
                                ft.getToAccount(), ft.getOriginalAmount(), ft.getAmount(), ft.getFromCurrency(), ft.getToCurrency(),
                                ft.getType()));
                        outputCollector.emit(new Values(ft2.getFtowner(), ft2.getTransactionAmount(), ft2.getId(),
                                ft2.getTimestamp().getTime(), ft2.getSender(), ft2.getReceiver(), ft2.getTitle(),
                                ft2.getFromAccount(), ft2.getToAccount(), ft2.getOriginalAmount(), ft2.getAmount(),
                                ft2.getFromCurrency(), ft2.getToCurrency(), ft2.getType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("ftowner", "ftamount", "id", "fttimestamp", "sender", "receiver", "title",
                "fromaccount", "toaccount", "originalamount", "amount", "fromcurrency", "tocurrency", "type"));
    }
}
