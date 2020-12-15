package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ShardingConfiguration {
    private String schemeName = "skywalking";

    private Map<String, ShardingDatasource> dataSources;

    private List<ShardingRule> rules;
}
