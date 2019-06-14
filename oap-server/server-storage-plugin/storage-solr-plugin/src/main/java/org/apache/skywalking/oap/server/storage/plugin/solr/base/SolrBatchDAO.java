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

package org.apache.skywalking.oap.server.storage.plugin.solr.base;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.skywalking.oap.server.core.storage.IBatchDAO;
import org.apache.skywalking.oap.server.storage.plugin.solr.SolrCollInpDoc;
import org.apache.skywalking.oap.server.storage.plugin.solr.SolrConnector;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SolrBatchDAO extends SolrDAO implements IBatchDAO {

    public SolrBatchDAO(SolrConnector client) {
        super(client);
    }

    @Override
    public void batchPersistence(List<?> list) {
        // ? : SolrInputDocument

        Map<String, Collection<SolrInputDocument>> collectionMap = Maps.newHashMap();
        Multimap<String, SolrInputDocument> docListMap = Multimaps.newListMultimap(collectionMap, new Supplier<List<SolrInputDocument>>() {
            @Override
            public List<SolrInputDocument> get() {
                return new ArrayList<SolrInputDocument>();
            }
        });
        list.forEach(e -> {
            SolrCollInpDoc cd = (SolrCollInpDoc) e;
            docListMap.put(cd.getCollection(), cd.getDoc());
        });

        collectionMap.forEach((k, v) -> {
            try {
                getClient().upsert(k, v);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            }
        });
    }

}
