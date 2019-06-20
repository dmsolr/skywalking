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

import org.apache.skywalking.oap.server.core.analysis.metrics.Metrics;
import org.apache.skywalking.oap.server.core.analysis.record.Record;
import org.apache.skywalking.oap.server.core.register.RegisterSource;
import org.apache.skywalking.oap.server.core.storage.*;
import org.apache.skywalking.oap.server.storage.plugin.solr.SolrConnector;

public class SolrStorageDAO extends SolrDAO implements StorageDAO {

    public SolrStorageDAO(SolrConnector client) {
        super(client);
    }

    @Override
    public IMetricsDAO newMetricsDao(StorageBuilder<Metrics> storageBuilder) {
        return new SolrMertricsDAO(getClient(), storageBuilder);
    }

    @Override
    public IRegisterDAO newRegisterDao(StorageBuilder<RegisterSource> storageBuilder) {
        return new SolrRegisterDAO(getClient(), storageBuilder);
    }

    @Override
    public IRecordDAO newRecordDao(StorageBuilder<Record> storageBuilder) {
        return new SolrRecordDAO(getClient(), storageBuilder);
    }

}
