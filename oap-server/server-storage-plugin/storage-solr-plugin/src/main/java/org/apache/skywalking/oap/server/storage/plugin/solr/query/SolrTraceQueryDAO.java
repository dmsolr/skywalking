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

import org.apache.skywalking.oap.server.core.analysis.manual.segment.SegmentRecord;
import org.apache.skywalking.oap.server.core.query.entity.QueryOrder;
import org.apache.skywalking.oap.server.core.query.entity.Span;
import org.apache.skywalking.oap.server.core.query.entity.TraceBrief;
import org.apache.skywalking.oap.server.core.query.entity.TraceState;
import org.apache.skywalking.oap.server.core.storage.query.ITraceQueryDAO;

import java.io.IOException;
import java.util.List;

public class SolrTraceQueryDAO implements ITraceQueryDAO {
    @Override
    public TraceBrief queryBasicTraces(long l, long l1, long l2, long l3, String s, int i, int i1, int i2, String s1, int i3, int i4, TraceState traceState, QueryOrder queryOrder) throws IOException {
        return null;
    }

    @Override
    public List<SegmentRecord> queryByTraceId(String s) throws IOException {
        return null;
    }

    @Override
    public List<Span> doFlexibleTraceQuery(String s) throws IOException {
        return null;
    }
}
