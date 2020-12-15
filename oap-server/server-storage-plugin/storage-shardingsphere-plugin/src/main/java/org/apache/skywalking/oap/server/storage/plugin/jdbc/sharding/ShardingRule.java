package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import lombok.Data;

import javax.crypto.KeyGenerator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ShardingRule {

    private Map<String, ShardingTable> tables;
}


@Data
class ShardingTable {
    private String actualDataNodes;
    private ShardingTableStrategy tableStrategy;
    private ShardingKeyGeneratorStrategy keyGeneratorStrategy;

}

@Data
class ShardingTableStrategy {

}

@Data
class ShardingKeyGeneratorStrategy {
    private List<String> column;
    private String keyGeneratorName;
}

