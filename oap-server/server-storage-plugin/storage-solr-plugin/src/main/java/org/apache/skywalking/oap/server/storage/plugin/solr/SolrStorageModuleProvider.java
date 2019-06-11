package org.apache.skywalking.oap.server.storage.plugin.solr;

import org.apache.skywalking.oap.server.core.storage.IBatchDAO;
import org.apache.skywalking.oap.server.core.storage.StorageModule;
import org.apache.skywalking.oap.server.library.module.ModuleConfig;
import org.apache.skywalking.oap.server.library.module.ModuleDefine;
import org.apache.skywalking.oap.server.library.module.ModuleProvider;
import org.apache.skywalking.oap.server.library.module.ModuleStartException;
import org.apache.skywalking.oap.server.library.module.ServiceNotProvidedException;
import org.apache.skywalking.oap.server.storage.plugin.solr.base.SolrBatchDAO;

public class SolrStorageModuleProvider extends ModuleProvider {
	final SolrStorageModuleConfig config;
	SolrConnector connector;

	public SolrStorageModuleProvider() {
		super();
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
		//
	}

	@Override
	public void prepare() throws ServiceNotProvidedException, ModuleStartException {
		this.registerServiceImplementation(IBatchDAO.class, new SolrBatchDAO());
	}

	@Override
	public String[] requiredModules() {
		return null;
	}

	@Override
	public void start() throws ServiceNotProvidedException, ModuleStartException {
		
	}

}
