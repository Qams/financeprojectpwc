package pl.edu.agh;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.cassandra.bolt.CassandraWriterBolt;
import org.apache.storm.generated.Bolt;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import pl.edu.agh.bolt.ExchangeRateChangeBolt;
import pl.edu.agh.bolt.PrintingBolt;
import pl.edu.agh.bolt.ResolveBolt;

import java.util.UUID;

import static org.apache.storm.cassandra.DynamicStatementBuilder.async;
import static org.apache.storm.cassandra.DynamicStatementBuilder.fields;
import static org.apache.storm.cassandra.DynamicStatementBuilder.simpleQuery;

public class MainTopology {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();

        BrokerHosts hosts = new ZkHosts("localhost:2181");
//        ((ZkHosts) hosts).refreshFreqSecs = 20000;
        SpoutConfig spoutConfig = new SpoutConfig(hosts, "exchange-rate", "/" + "exchange-rate", UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        spoutConfig.ignoreZkOffsets = true;
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

        builder.setSpout("kafkaSpout", kafkaSpout);
        ResolveBolt resolveBolt = new ResolveBolt();
        builder.setBolt("resolveBolt", resolveBolt)
                .shuffleGrouping("kafkaSpout");
        PrintingBolt printingBolt = new PrintingBolt();
        builder.setBolt("printingBolt", printingBolt)
                .shuffleGrouping("resolveBolt");
        builder.setBolt("cassandraBolt", new CassandraWriterBolt(
                async(
                        simpleQuery("INSERT INTO exchangerate (id, currencytime, ask, askvolume, bid, bidvolume, fromcurrency, tocurrency, exchange) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")
                                .with(
                                        fields("id", "timestamp", "ask", "askvolume", "bid", "bidvolume", "from", "to", "exchange")
                                )
                )
        )).shuffleGrouping("resolveBolt");
        BaseWindowedBolt aggregating = new ExchangeRateChangeBolt()
                .withTimestampField("timestamp")
                .withLag(BaseWindowedBolt.Duration.seconds(1))
                .withWindow(BaseWindowedBolt.Duration.seconds(15), BaseWindowedBolt.Duration.seconds(15));
        builder.setBolt("aggregatingBolt", aggregating)
                .shuffleGrouping("resolveBolt");
        builder.setBolt("cassandraWindowedBolt", new CassandraWriterBolt(
                async(
                        simpleQuery("INSERT INTO exchangeratewindowed (starttime, endtime, exchange, changes, maxask, maxbid, minask, minbid) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")
                                .with(
                                        fields("starttime", "endtime", "exchange", "changes", "maxask", "maxbid", "minask", "minbid")
                                )
                )
        )).shuffleGrouping("aggregatingBolt");

        Config config = new Config();
        config.put("cassandra.keyspace", "voting");
//        config.put("cassandra.port", "7199");
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Test", config, builder.createTopology());
    }

}
