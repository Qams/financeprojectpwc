package pl.edu.agh.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import pl.edu.agh.model.ExchangeRate;
import pl.edu.agh.model.HelloClient;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PrintingBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    private HelloClient helloClient = new HelloClient();

    private ListenableFuture<StompSession> f;

    private StompSession stompSession;

    private Date lastChange = new Date();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        f = helloClient.connect();
        try {
            stompSession = f.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {
        outputCollector.ack(tuple);
        System.out.println("ASK: " + tuple);
        ExchangeRate rate = ExchangeRate.builder()
                .id(tuple.getStringByField("id"))
                .timestamp(new Date(tuple.getLongByField("timestamp")))
                .ask(tuple.getDoubleByField("ask"))
                .bid(tuple.getDoubleByField("bid"))
                .askVolume(tuple.getDoubleByField("askvolume"))
                .bidVolume(tuple.getDoubleByField("bidvolume"))
                .from(tuple.getStringByField("from"))
                .to(tuple.getStringByField("to"))
                .exchange(tuple.getStringByField("exchange"))
                .build();
//        if((lastChange.getTime() + 800) < (new Date().getTime()) && rate.getFrom().equals("EUR") && rate.getTo().equals("GBP") ) {
//        helloClient.sendHello(stompSession, tuple.getStringByField("id"));
            lastChange = new Date();
            helloClient.sendHello(stompSession, rate);
//        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
