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

package org.apache.skywalking.oap.server.storage.plugin.solr;

import com.google.common.collect.Sets;
import org.apache.skywalking.oap.server.core.storage.StorageException;
import org.apache.skywalking.oap.server.core.storage.model.Model;
import org.apache.skywalking.oap.server.core.storage.model.ModelInstaller;
import org.apache.skywalking.oap.server.library.client.Client;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.CoreStatus;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class SolrTableInstall extends ModelInstaller {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public SolrTableInstall(ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    protected boolean isExists(Client client, Model model) throws StorageException {
        SolrConnector connector = (SolrConnector) client;
        if (connector.isCloudMode()) {
            return isCollectionExists(connector, model);
        }
        return isCoreExists(connector, model);
    }

    private boolean isCoreExists(SolrConnector client, Model model) throws StorageException {
        try {
            CoreStatus status = CoreAdminRequest.getCoreStatus(model.getName(), client.getClient());
            return (null != status && status.getCoreStartTime() != null);
        } catch (SolrServerException | IOException e) {
            throw new StorageException("", e);
        }
    }
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile boolean queried = false;
    private Set<String> collections = null;

    private boolean isCollectionExists(SolrConnector client, Model model) throws StorageException {
        if (queried) {
            return collections.contains(model.getName());
        }
        while (!queried) try {
            latch.await();
        } catch (InterruptedException ignore) { }

        try {
            Iterable _collections = CollectionAdminRequest.listCollections(client.getClient());
            collections = Sets.immutableEnumSet(_collections);
            return collections.contains(model.getName());
        } catch (IOException | SolrServerException e) {
            throw new StorageException("", e);
        } finally {
            latch.countDown(); // release all blocked threads, then closes.
            queried = true;
        }
    }

    @Override
    protected void createTable(Client client, Model model) throws StorageException {
        SolrConnector connector = (SolrConnector) client;
        if (connector.isCloudMode()) {
            throw new StorageException("not support! U must create core by yourself.");
        }

        SolrClient solrClient = connector.getClient();
        try {
            CollectionAdminRequest.Create create = CollectionAdminRequest.createCollection(model.getName(), "_default", 1, 1);
            CollectionAdminResponse process = create.setMaxShardsPerNode(1).process(solrClient);
            LOG.info(process.toString());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        LOG.warn(model.getName() + "\t" + model.getDownsampling().getName());
    }

//    private Set<String> configSets = Sets.newCopyOnWriteArraySet();
    private boolean isConfigExists(SolrClient client, Model model) throws IOException, SolrServerException {
        ConfigSetAdminResponse.List process = new ConfigSetAdminRequest.List().process(client);
        Set<String> configSets = Sets.newHashSet(process.getConfigSets());

        return configSets.contains(model.getName());
    }
}
