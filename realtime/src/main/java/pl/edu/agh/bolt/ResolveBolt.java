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
import pl.edu.agh.model.ExchangeRate;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class ResolveBolt extends BaseRichBolt {

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
                        ExchangeRate rate = ExchangeRate.builder()
                                .id(rootNode.get("id").asText())
                                .timestamp(new Date(rootNode.get("timestamp").asLong()))
                                .ask(rootNode.get("ask").asDouble())
                                .bid(rootNode.get("bid").asDouble())
                                .askVolume(rootNode.get("askVolume").asDouble())
                                .bidVolume(rootNode.get("bidVolume").asDouble())
                                .from(rootNode.get("from").asText())
                                .to(rootNode.get("to").asText())
                                .build();
                        System.out.println(rate.getId() + ", DATE: " + new Date().getTime());
                        outputCollector.emit(new Values(rate.getId(), rate.getTimestamp().getTime(),
                                rate.getAsk(), rate.getBid(), rate.getAskVolume(), rate.getBidVolume(), rate.getFrom(),
                                rate.getTo(), rate.getFrom()+rate.getTo()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        System.out.println(tuple);
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("id", "timestamp", "ask", "bid", "askvolume", "bidvolume", "from", "to", "exchange"));
    }
}
