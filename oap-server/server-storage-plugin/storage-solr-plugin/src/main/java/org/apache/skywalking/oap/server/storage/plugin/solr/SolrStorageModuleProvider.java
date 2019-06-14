package org.apache.skywalking.oap.server.storage.plugin.solr;

import org.apache.skywalking.oap.server.core.storage.IBatchDAO;
import org.apache.skywalking.oap.server.core.storage.StorageDAO;
import org.apache.skywalking.oap.server.core.storage.StorageModule;
import org.apache.skywalking.oap.server.library.module.*;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrBatchDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrStorageDAO;

public class SolrStorageModuleProvider extends ModuleProvider {
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
    }

    @Override
    public String[] requiredModules() {
        return null;
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {

    }

}
