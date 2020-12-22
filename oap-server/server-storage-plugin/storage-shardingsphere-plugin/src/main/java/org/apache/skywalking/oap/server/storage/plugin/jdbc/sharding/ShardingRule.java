package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ShardingRule {

    private Map<String, ShardingTable> tables;

    private DatabaseStrategy defaultDatabaseStrategy;

    private TableStrategy defaultTableStrategy;

    private Map<String, ShardingAlgorithm> shardingAlgorithms;

    @Data
    public static class ShardingTable {
        private String actualDataNodes;
        private ShardingTableStrategy tableStrategy;
        private ShardingKeyGeneratorStrategy keyGeneratorStrategy;
    }

    @Data
    public static class DatabaseStrategy {
//        private StandardDatabaseStrategy standard;
    }

    @Data
    public static class TableStrategy {

    }

    @Data
    public static class ShardingAlgorithm {
        private String type;
        private Map<String, Object> props;
    }
}

@Data
class ShardingTableStrategy {

}

@Data
class ShardingKeyGeneratorStrategy {
    private List<String> column;
    private String keyGeneratorName;
}

