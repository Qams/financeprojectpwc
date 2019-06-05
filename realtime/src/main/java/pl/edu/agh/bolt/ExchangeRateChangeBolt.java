package pl.edu.agh.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class ExchangeRateChangeBolt extends BaseWindowedBolt {

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("starttime", "endtime", "exchange", "changes", "maxask", "maxbid", "minask", "minbid"));
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        List<Tuple> tuples = tupleWindow.get();
        if(tuples.size() > 0) {
            tuples.sort(Comparator.comparing(this::getTimestamp));
            Long beginningTimestamp = getTimestamp(tuples.get(0));
            Long endTimestamp = getTimestamp(tuples.get(tuples.size() - 1));
            Map<String, Set<Tuple>> from =
                    tuples.stream()
                            .collect(groupingBy(tuple -> tuple.getStringByField("from") + tuple.getStringByField("to"), toSet()));


            for (Map.Entry<String, Set<Tuple>> entry : from.entrySet())
            {
                Set<Tuple> set = entry.getValue();
                Tuple tupleMaxAsk = Collections.max(set, Comparator.comparing(e -> ((Double) e.getValueByField("ask"))));
                Tuple tupleMaxBid = Collections.max(set, Comparator.comparing(e -> ((Double) e.getValueByField("bid"))));
                Tuple tupleMinAsk = Collections.min(set, Comparator.comparing(e -> ((Double) e.getValueByField("ask"))));
                Tuple tupleMinBid = Collections.min(set, Comparator.comparing(e -> ((Double) e.getValueByField("bid"))));

                System.out.println("EXCHANGE RATE = " + entry.getKey());
                System.out.println("TUPLE MAX ASK = " + tupleMaxAsk);
                System.out.println("TUPLE MAX BID = " + tupleMaxBid);
                System.out.println("TUPLE MIN ASK = " + tupleMinAsk);
                System.out.println("TUPLE MIN BID = " + tupleMinBid);
                System.out.print("SIZE: " + set.size());

                outputCollector.emit(new Values(beginningTimestamp, endTimestamp, entry.getKey(), set.size(),
                        ((double) tupleMaxAsk.getValueByField("ask")), ((double) tupleMaxBid.getValueByField("bid")),
                        ((double) tupleMinAsk.getValueByField("ask")), ((double) tupleMinBid.getValueByField("bid"))));
            }
            tuples.forEach(t -> outputCollector.ack(t));

            System.out.println("WINDOWED: " + new Date() + " " + new Date(beginningTimestamp) + " TO: " + new Date(endTimestamp) + " = " + from);
            System.out.println("WINSIZE: " + tuples.size());
            //            outputCollector.ack(tuple);
        }
    }

    private Long getTimestamp(Tuple tuple) {
        return tuple.getLongByField("timestamp");
    }
}
