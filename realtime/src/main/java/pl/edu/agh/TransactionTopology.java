package pl.edu.agh;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.cassandra.bolt.CassandraWriterBolt;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import pl.edu.agh.bolt.ExchangeRateChangeBolt;
import pl.edu.agh.bolt.ResolveTransactionBolt;
import pl.edu.agh.bolt.TransactionChangeBolt;

import java.util.UUID;

import static org.apache.storm.cassandra.DynamicStatementBuilder.async;
import static org.apache.storm.cassandra.DynamicStatementBuilder.fields;
import static org.apache.storm.cassandra.DynamicStatementBuilder.simpleQuery;

public class TransactionTopology {
    public static void main(String[] args) {
        TopologyBuilder builderTransaction = new TopologyBuilder();
        BrokerHosts hosts = new ZkHosts("localhost:2181");

        SpoutConfig spoutTransactionConfig = new SpoutConfig(hosts, "fin-transaction", "/" + "fin-transaction", UUID.randomUUID().toString());
        spoutTransactionConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutTransactionConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        spoutTransactionConfig.ignoreZkOffsets = true;
        KafkaSpout kafkaTransactionSpout = new KafkaSpout(spoutTransactionConfig);

        builderTransaction.setSpout("kafkaTransactionSpout", kafkaTransactionSpout);
        ResolveTransactionBolt resolveBolt = new ResolveTransactionBolt();
        builderTransaction.setBolt("resolveTransactionBolt", resolveBolt)
                .shuffleGrouping("kafkaTransactionSpout");

        BaseWindowedBolt aggregating = new TransactionChangeBolt()
                .withTimestampField("fttimestamp")
                .withWindow(BaseWindowedBolt.Duration.seconds(15), BaseWindowedBolt.Duration.seconds(15));
        builderTransaction.setBolt("aggregatingTransactionBolt", aggregating)
                .shuffleGrouping("resolveTransactionBolt");
        builderTransaction.setBolt("cassandraTransactionBolt", new CassandraWriterBolt(
                async(
                        simpleQuery("INSERT INTO fintransaction (ftowner, ftamount, id, fttimestamp, sender, receiver, title, fromaccount, toaccount, originalamount, amount, fromcurrency, tocurrency, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
                                .with(
                                        fields("ftowner", "ftamount", "id", "fttimestamp", "sender", "receiver", "title", "fromaccount", "toaccount", "originalamount", "amount", "fromcurrency", "tocurrency", "type")
                                )
                )
        )).shuffleGrouping("resolveTransactionBolt");

        Config config = new Config();
        config.put("cassandra.keyspace", "voting");
//        config.put("cassandra.port", "7199");
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("TransactionTopology", config, builderTransaction.createTopology());
    }
}
