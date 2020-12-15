package org.apache.skywalking.oap.server.storage.plugin.jdbc.sharding;

import lombok.Data;

@Data
public class ShardingDatasource {

    private String url;
    private String username;
    private String password;
    private int connectionTimeoutMilliseconds = 30000;
    private int idleTimeoutMilliseconds = 60000;
    private int maxLifetimeMilliseconds = 1800000;
    private int maxPoolSize = 50;
    private int minPoolSize = 1;
    private int maintenanceIntervalMilliseconds = 30000;

}
