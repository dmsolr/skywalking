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

package org.apache.skywalking.oap.server.storage.plugin.solr.query;

import org.apache.skywalking.oap.server.core.query.entity.Database;
import org.apache.skywalking.oap.server.core.query.entity.Endpoint;
import org.apache.skywalking.oap.server.core.query.entity.Service;
import org.apache.skywalking.oap.server.core.query.entity.ServiceInstance;
import org.apache.skywalking.oap.server.core.storage.query.IMetadataQueryDAO;

import java.io.IOException;
import java.util.List;

public class SolrMetadataQueryDAO implements IMetadataQueryDAO {
    @Override
    public int numOfService(long l, long l1) throws IOException {
        return 0;
    }

    @Override
    public int numOfEndpoint(long l, long l1) throws IOException {
        return 0;
    }

    @Override
    public int numOfConjectural(long l, long l1, int i) throws IOException {
        return 0;
    }

    @Override
    public List<Service> getAllServices(long l, long l1) throws IOException {
        return null;
    }

    @Override
    public List<Database> getAllDatabases() throws IOException {
        return null;
    }

    @Override
    public List<Service> searchServices(long l, long l1, String s) throws IOException {
        return null;
    }

    @Override
    public Service searchService(String s) throws IOException {
        return null;
    }

    @Override
    public List<Endpoint> searchEndpoint(String s, String s1, int i) throws IOException {
        return null;
    }

    @Override
    public List<ServiceInstance> getServiceInstances(long l, long l1, String s) throws IOException {
        return null;
    }
}
