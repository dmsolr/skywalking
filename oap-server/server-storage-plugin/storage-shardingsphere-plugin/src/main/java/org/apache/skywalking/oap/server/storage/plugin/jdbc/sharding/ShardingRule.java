package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ShardingRule {

    // tables:
    private Map<String, ShardingTable> tables;

    // bindingTables
    // ----

    // defaultDatabaseStrategy
    private DatabaseStrategy defaultDatabaseStrategy;

    // defaultTableStrategy
    private TableStrategy defaultTableStrategy;

    // shardingAlgorithms
    private Map<String, ShardingAlgorithm> shardingAlgorithms;

    // keyGenerators

    @Data
    public static class ShardingTable {
        private String actualDataNodes;
        private ShardingTableStrategy tableStrategy;
        private ShardingKeyGeneratorStrategy keyGeneratorStrategy;
    }

    @Data
    public static class DatabaseStrategy {
//        private StandardDatabaseStrategy standard;
        // standard:
        // none:

        public static class StandardDatabaseStrategy {
//            shardingColumn: user_id
//            shardingAlgorithmName: database_inline
        }

        public static class NoneDatabaseStrategy {

        }
    }

    @Data
    public static class TableStrategy {

    }

    @Data
    public static class ShardingAlgorithm {
        private String type;
        private Map<String, Object> props;
    }

    @Data
    class ShardingTableStrategy {

    }

    @Data
    class ShardingKeyGeneratorStrategy {
        private List<String> column;
        private String keyGeneratorName;
    }

}
