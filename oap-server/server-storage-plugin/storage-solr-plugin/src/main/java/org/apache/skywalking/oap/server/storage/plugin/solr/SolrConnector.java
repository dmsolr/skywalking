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

import org.apache.skywalking.oap.server.library.client.Client;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;
import java.util.Optional;

public class SolrConnector implements Client {
    private SolrClient client = null;
    private boolean isCloudMode = false;

    public SolrConnector(SolrStorageModuleConfig config) {
        // Cloud Mode
        if (config.isCloudMode()) {
            // by zk
            CloudSolrClient client;
            if (config.useZookeeper()) {
                client = new CloudSolrClient.Builder(config.getZKHosts(), Optional.of(config.getChroot()))
                        .build();
            } else {
                client = new CloudSolrClient.Builder(config.getHosts()).build();
            }
//            client.setDefaultCollection(config.getCollection());
        }
        else {
            client = new HttpSolrClient.Builder("").build();
        }
    }

    @Override
    public void connect() throws IOException {
        if (isCloudMode) {
            ((CloudSolrClient) client).connect();
        }
    }

    @Override
    public void shutdown() throws IOException {
        client.close();
    }
}
