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

package org.apache.skywalking.oap.server.storage.plugin.influxdb.query;

import org.apache.skywalking.oap.server.core.alarm.AlarmRecord;
import org.apache.skywalking.oap.server.core.query.entity.AlarmMessage;
import org.apache.skywalking.oap.server.core.query.entity.Alarms;
import org.apache.skywalking.oap.server.core.query.entity.Scope;
import org.apache.skywalking.oap.server.core.storage.query.IAlarmQueryDAO;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxClient;
import org.elasticsearch.common.Strings;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.querybuilder.SelectQueryImpl;
import org.influxdb.querybuilder.WhereQueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.*;

public class AlarmQuery implements IAlarmQueryDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final InfluxClient client;

    public AlarmQuery(InfluxClient client) {
        this.client = client;
    }

    @Override
    public Alarms getAlarm(Integer scopeId, String keyword, int limit, int from, long startTB, long endTB) throws IOException {

        WhereQueryImpl<SelectQueryImpl> query1 = select()
                .function("top", AlarmRecord.START_TIME, limit + from).as(AlarmRecord.START_TIME)
                .column(AlarmRecord.ID0)
                .column(AlarmRecord.ALARM_MESSAGE)
                .column(AlarmRecord.SCOPE)
                .from(client.getDatabase(), AlarmRecord.INDEX_NAME)
                .where();
        if (startTB > 0 && endTB > 0) {
            query1.and(gte(InfluxClient.TIME, InfluxClient.timeInterval(startTB)))
                    .and(lte(InfluxClient.TIME, InfluxClient.timeInterval(endTB)));
        }
        if (!Strings.isNullOrEmpty(keyword)) {
            query1.and(regex(AlarmRecord.ALARM_MESSAGE, keyword));
        }
        if (Objects.nonNull(scopeId)) {
            query1.and(eq(AlarmRecord.SCOPE, scopeId));
        }

        WhereQueryImpl<SelectQueryImpl> query2 = select().count(AlarmRecord.ID0).from(client.getDatabase(), AlarmRecord.INDEX_NAME).where();
        query1.getClauses().forEach(clause -> {
            query2.where(clause);
        });

        Query query = new Query(query2.getCommand() + query1.getCommand());
        List<QueryResult.Result> results = client.query(query);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: {} \nresult set: {}", query.getCommand(), results);
        }
        if (results.size() != 2) {
            throw new IOException("We expect to get 2 Results, but it is " + results.size());
        }
        List<QueryResult.Series> series = results.get(1).getSeries();
        if (series == null || series.isEmpty()) {
            return new Alarms();
        }
        List<QueryResult.Series> counter = results.get(0).getSeries();
        Alarms alarms = new Alarms();
        alarms.setTotal(((Number) counter.get(0).getValues().get(0).get(1)).intValue());

        series.get(0).getValues().stream().sorted((a, b) -> {
            return Long.compare((long) b.get(1), (long) a.get(1));
        }).skip(from).forEach(values -> {
            final int sid = (int) values.get(4);
            Scope scope = Scope.Finder.valueOf(sid);

            AlarmMessage message = new AlarmMessage();
            message.setStartTime((long) values.get(1));
            message.setId((String) values.get(2));
            message.setMessage((String) values.get(3));
            message.setScope(scope);
            message.setScopeId(sid);

            alarms.getMsgs().add(message);
        });
        return alarms;
    }
}