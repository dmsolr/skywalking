package org.apache.skywalking.oap.server.storage.plugin.solr;

import org.apache.skywalking.oap.server.core.storage.*;
import org.apache.skywalking.oap.server.core.storage.cache.IEndpointInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.INetworkAddressInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.IServiceInstanceInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.cache.IServiceInventoryCacheDAO;
import org.apache.skywalking.oap.server.core.storage.query.*;
import org.apache.skywalking.oap.server.library.module.*;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrBatchDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrHistoryDeleteDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrMertricsDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrStorageDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.cache.SolrEndpointInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.cache.SolrNetworkAddressInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.cache.SolrServiceInventoryCacheDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.lock.SolrRegisterLockDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class SolrStorageModuleProvider extends ModuleProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    final SolrStorageModuleConfig config;
    SolrConnector connector;

    public SolrStorageModuleProvider() {
        this.config = new SolrStorageModuleConfig();
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return config;
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return StorageModule.class;
    }

    @Override
    public String name() {
        return "Solr";
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        this.registerServiceImplementation(IBatchDAO.class, new SolrBatchDAO(connector));
        this.registerServiceImplementation(StorageDAO.class, new SolrStorageDAO(connector));

        this.registerServiceImplementation(IRegisterLockDAO.class, new SolrRegisterLockDAO());
        this.registerServiceImplementation(IHistoryDeleteDAO.class, new SolrHistoryDeleteDAO(connector)); // getManager(), connector, null));

        this.registerServiceImplementation(IServiceInventoryCacheDAO.class, new SolrServiceInventoryCacheDAO());
        this.registerServiceImplementation(IServiceInstanceInventoryCacheDAO.class, new SolrEndpointInventoryCacheDAO());
        this.registerServiceImplementation(IEndpointInventoryCacheDAO.class, new SolrEndpointInventoryCacheDAO());
        this.registerServiceImplementation(INetworkAddressInventoryCacheDAO.class, new SolrNetworkAddressInventoryCacheDAO());

        this.registerServiceImplementation(ITopologyQueryDAO.class, new SolrTopologyQueryDAO());
//        this.registerServiceImplementation(IMetricsQueryDAO.class, new SolrMertricsDAO(connector));
//        this.registerServiceImplementation(ITraceQueryDAO.class, new SolrTraceQueryDAO(connector));
//        this.registerServiceImplementation(IMetadataQueryDAO.class, new SolrMertricsDAO(connector));
        this.registerServiceImplementation(IAggregationQueryDAO.class, new SolrAggregationQueryDAO());
        this.registerServiceImplementation(IAlarmQueryDAO.class, new SolrAlarmQueryDAO());
        this.registerServiceImplementation(ITopNRecordsQueryDAO.class, new SolrTopNRecordsQueryDAO());
        this.registerServiceImplementation(ILogQueryDAO.class, new SolrLogQueryDAO());
    }

    @Override
    public String[] requiredModules() {
        return null;
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        try {
            connector.connect();

            try {
                new SolrTableInstall(getManager()).install(connector);

//                new SolrRegisterLockInstaller
            } catch (StorageException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

}
