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

import com.google.common.collect.Maps;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.Map;

public class DocumentBuilder {

    public static final SolrInputDocument toDocument(Map<String, Object> map) {
        SolrInputDocument document = new SolrInputDocument();
        map.forEach((k, v) -> {
            document.addField(k, v);
        });
        return document;
    }

    public static final Map<String, Object> toMap(SolrDocument document) {
        Map<String, Object> map = Maps.newHashMap();
        document.forEach((k, v) -> {
            map.put(k, v);
        });
        return map;
    }
}
