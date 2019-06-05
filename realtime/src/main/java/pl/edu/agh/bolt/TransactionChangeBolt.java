package pl.edu.agh.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import pl.edu.agh.model.FinTransactionWindowed;
import pl.edu.agh.model.TransactionsClient;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class TransactionChangeBolt extends BaseWindowedBolt {

    private OutputCollector outputCollector;

    private TransactionsClient transactionsClient = new TransactionsClient();

    private ListenableFuture<StompSession> f;

    private StompSession stompSession;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
        f = transactionsClient.connect();
        try {
            stompSession = f.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        System.out.println("HELLO");
        List<FinTransactionWindowed> incomeWindow = new LinkedList<>();
        List<FinTransactionWindowed> outcomeWindow = new LinkedList<>();
        List<Tuple> tuples = tupleWindow.get();
        if (tuples.size() > 0) {
            tuples.sort(Comparator.comparing(this::getTimestamp));
            Long beginningTimestamp = getTimestamp(tuples.get(0));
            Long endTimestamp = getTimestamp(tuples.get(tuples.size() - 1));
            Map<String, Set<Tuple>> outcome =
                    tuples.stream().filter(x -> x.getStringByField("ftowner").equals(x.getStringByField("fromaccount")))
                            .collect(groupingBy(tuple -> tuple.getStringByField("fromcurrency"), toSet()));
            Map<String, Set<Tuple>> income =
                    tuples.stream().filter(x -> x.getStringByField("ftowner").equals(x.getStringByField("toaccount")))
                            .collect(groupingBy(tuple -> tuple.getStringByField("tocurrency"), toSet()));
            Map<String, FinTransactionWindowed> map = new HashMap<>();

            for (Map.Entry<String, Set<Tuple>> entry : outcome.entrySet()) {
                Set<Tuple> set = entry.getValue();
                FinTransactionWindowed outcomeStats = FinTransactionWindowed.builder()
                        .dateStart(beginningTimestamp)
                        .dateEnd(endTimestamp)
                        .currency(entry.getKey())
                        .incomeSumAmount(0.0)
                        .incomeTransactions(0L)
                        .outcomeTransactions((long) set.size())
                        .outcomeSumAmount(set.stream().mapToDouble(x -> x.getDoubleByField("originalamount")).sum())
                        .build();
                map.put(entry.getKey(), outcomeStats);
            }
            for (Map.Entry<String, Set<Tuple>> entry : income.entrySet()) {
                Set<Tuple> set = entry.getValue();
                if(map.containsKey(entry.getKey())) {
                    FinTransactionWindowed finTransactionWindowed = map.get(entry.getKey());
                    finTransactionWindowed.setIncomeSumAmount(set.stream().mapToDouble(x -> x.getDoubleByField("amount")).sum());
                    finTransactionWindowed.setIncomeTransactions((long) set.size());
                    transactionsClient.sendHello(stompSession, finTransactionWindowed);
                }
                else {
                    FinTransactionWindowed incomeStats = FinTransactionWindowed.builder()
                            .dateStart(beginningTimestamp)
                            .dateEnd(endTimestamp)
                            .outcomeSumAmount(0.0)
                            .outcomeTransactions(0L)
                            .currency(entry.getKey())
                            .incomeTransactions((long) set.size())
                            .incomeSumAmount(set.stream().mapToDouble(x -> x.getDoubleByField("amount")).sum())
                            .build();
                    transactionsClient.sendHello(stompSession, incomeStats);
                }

                // TODO websocket which will send this data
            }

        }
    }

    private Long getTimestamp(Tuple tuple) {
        return tuple.getLongByField("fttimestamp");
    }
}
