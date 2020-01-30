/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.storage.plugin.influxdb;

import java.io.IOException;
import java.util.Properties;
import org.apache.skywalking.oap.server.core.CoreModule;
import org.apache.skywalking.oap.server.core.storage.IBatchDAO;
import org.apache.skywalking.oap.server.core.storage.IHistoryDeleteDAO;
import org.apache.skywalking.oap.server.core.storage.IRegisterLockDAO;
import org.apache.skywalking.oap.server.core.storage.StorageDAO;
import org.apache.skywalking.oap.server.core.storage.StorageException;
import org.apache.skywalking.oap.server.core.storage.StorageModule;
import org.apache.skywalking.oap.server.core.storage.cache.IEndpointInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.INetworkAddressInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.IServiceInstanceInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.IServiceInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.model.Model;
import org.apache.skywalking.oap.server.core.storage.model.ModelInstaller;
import org.apache.skywalking.oap.server.core.storage.profile.IProfileTaskLogQueryDAO;
import org.apache.skywalking.oap.server.core.storage.profile.IProfileTaskQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.IAggregationQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.IAlarmQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.ILogQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.IMetadataQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.IMetricsQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.ITopNRecordsQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.ITopologyQueryDAO;
import org.apache.skywalking.oap.server.core.storage.query.ITraceQueryDAO;
import org.apache.skywalking.oap.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import org.apache.skywalking.oap.server.library.module.ModuleConfig;
import org.apache.skywalking.oap.server.library.module.ModuleDefine;
import org.apache.skywalking.oap.server.library.module.ModuleProvider;
import org.apache.skywalking.oap.server.library.module.ModuleStartException;
import org.apache.skywalking.oap.server.library.module.ServiceNotProvidedException;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.base.BatchDAO;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.base.InfluxStorageDAO;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.installer.H2Installer;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.installer.MySQLInstaller;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.AggregationQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.AlarmQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.LogQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.MetricsQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.ProfileTaskLogQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.TopNRecordsQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.TopologyQuery;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.query.TraceQuery;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2EndpointInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2MetadataQueryDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2NetworkAddressInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2ProfileTaskQueryDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2RegisterLockDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2RegisterLockInstaller;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2ServiceInstanceInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.jdbc.h2.dao.H2ServiceInventoryCacheDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfluxStorageProvider extends ModuleProvider {
    private static final Logger logger = LoggerFactory.getLogger(InfluxStorageProvider.class);

    private InfluxStorageConfig config;
    private JDBCHikariCPClient client;
    private InfluxClient influxClient;
    private H2RegisterLockDAO lockDAO;

    public InfluxStorageProvider() {
        config = new InfluxStorageConfig();
    }

    @Override
    public String name() {
        return "influx";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return StorageModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException {
        Properties settings = new Properties();
        settings.setProperty("dataSourceClassName", config.getMetabaseDriver());
        settings.setProperty("dataSource.url", config.getMetabaseUrl());
        settings.setProperty("dataSource.user", config.getMetabaseUser());
        settings.setProperty("dataSource.password", config.getMetabasePassword());

        client = new JDBCHikariCPClient(settings);
        influxClient = new InfluxClient(config);

        this.registerServiceImplementation(IBatchDAO.class, new BatchDAO(influxClient));
        this.registerServiceImplementation(StorageDAO.class, new InfluxStorageDAO(client, influxClient));

        this.lockDAO = new H2RegisterLockDAO(client);
        this.registerServiceImplementation(IRegisterLockDAO.class, new H2RegisterLockDAO(client));

        this.registerServiceImplementation(IServiceInventoryCacheDAO.class, new H2ServiceInventoryCacheDAO(client));
        this.registerServiceImplementation(IServiceInstanceInventoryCacheDAO.class, new H2ServiceInstanceInventoryCacheDAO(client));
        this.registerServiceImplementation(IEndpointInventoryCacheDAO.class, new H2EndpointInventoryCacheDAO(client));
        this.registerServiceImplementation(INetworkAddressInventoryCacheDAO.class, new H2NetworkAddressInventoryCacheDAO(client));

        this.registerServiceImplementation(ITopologyQueryDAO.class, new TopologyQuery(influxClient));
        this.registerServiceImplementation(IMetricsQueryDAO.class, new MetricsQuery(influxClient));
        this.registerServiceImplementation(ITraceQueryDAO.class, new TraceQuery(influxClient));
        this.registerServiceImplementation(IMetadataQueryDAO.class, new H2MetadataQueryDAO(client, config.getMetadataQueryMaxSize()));
        this.registerServiceImplementation(IAggregationQueryDAO.class, new AggregationQuery(influxClient));
        this.registerServiceImplementation(IAlarmQueryDAO.class, new AlarmQuery(influxClient));
        this.registerServiceImplementation(ITopNRecordsQueryDAO.class, new TopNRecordsQuery(influxClient));
        this.registerServiceImplementation(ILogQueryDAO.class, new LogQuery(influxClient));

        this.registerServiceImplementation(IProfileTaskQueryDAO.class, new H2ProfileTaskQueryDAO(client));
        this.registerServiceImplementation(IProfileTaskLogQueryDAO.class, new ProfileTaskLogQuery(influxClient));

        this.registerServiceImplementation(IHistoryDeleteDAO.class, new IHistoryDeleteDAO() {
            @Override
            public void deleteHistory(Model model, String timeBucketColumnName) throws IOException {
                // need to do nothing here. InfluxDB eliminates the time-out live record by RetentionPolicy.
            }
        });
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {
            client.connect();
            influxClient.connect();

            ModelInstaller installer;
            if (config.getMetabaseType().equalsIgnoreCase("h2")) {
                installer = new H2Installer(getManager());
            } else {
                installer = new MySQLInstaller(getManager());
            }
            installer.install(client);
            new H2RegisterLockInstaller().install(client, lockDAO);
        } catch (StorageException e) {
            throw new ModuleStartException(e.getMessage(), e);
        }
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public String[] requiredModules() {
        return new String[]{CoreModule.NAME};
    }
}