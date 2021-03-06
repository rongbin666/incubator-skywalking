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

package org.apache.skywalking.apm.collector.storage.es.dao.amp;

import java.util.HashMap;
import java.util.Map;
import org.apache.skywalking.apm.collector.client.elasticsearch.ElasticSearchClient;
import org.apache.skywalking.apm.collector.core.annotations.trace.GraphComputingMetric;
import org.apache.skywalking.apm.collector.storage.es.base.dao.AbstractPersistenceEsDAO;
import org.apache.skywalking.apm.collector.storage.table.application.ApplicationMetric;
import org.apache.skywalking.apm.collector.storage.table.application.ApplicationMetricTable;
import org.apache.skywalking.apm.collector.storage.es.MetricTransformUtil;

/**
 * @author peng-yongsheng
 */
public abstract class AbstractApplicationMetricEsPersistenceDAO extends AbstractPersistenceEsDAO<ApplicationMetric> {

    AbstractApplicationMetricEsPersistenceDAO(ElasticSearchClient client) {
        super(client);
    }

    @Override protected final String timeBucketColumnNameForDelete() {
        return ApplicationMetricTable.TIME_BUCKET.getName();
    }

    @Override protected final ApplicationMetric esDataToStreamData(Map<String, Object> source) {
        ApplicationMetric applicationMetric = new ApplicationMetric();
        applicationMetric.setMetricId((String)source.get(ApplicationMetricTable.METRIC_ID.getName()));

        applicationMetric.setApplicationId(((Number)source.get(ApplicationMetricTable.APPLICATION_ID.getName())).intValue());

        MetricTransformUtil.INSTANCE.esDataToStreamData(source, applicationMetric);

        applicationMetric.setSatisfiedCount(((Number)source.get(ApplicationMetricTable.SATISFIED_COUNT.getName())).longValue());
        applicationMetric.setToleratingCount(((Number)source.get(ApplicationMetricTable.TOLERATING_COUNT.getName())).longValue());
        applicationMetric.setFrustratedCount(((Number)source.get(ApplicationMetricTable.FRUSTRATED_COUNT.getName())).longValue());

        return applicationMetric;
    }

    @Override protected final Map<String, Object> esStreamDataToEsData(ApplicationMetric streamData) {
        Map<String, Object> target = new HashMap<>();
        target.put(ApplicationMetricTable.METRIC_ID.getName(), streamData.getMetricId());

        target.put(ApplicationMetricTable.APPLICATION_ID.getName(), streamData.getApplicationId());

        MetricTransformUtil.INSTANCE.esStreamDataToEsData(streamData, target);

        target.put(ApplicationMetricTable.SATISFIED_COUNT.getName(), streamData.getSatisfiedCount());
        target.put(ApplicationMetricTable.TOLERATING_COUNT.getName(), streamData.getToleratingCount());
        target.put(ApplicationMetricTable.FRUSTRATED_COUNT.getName(), streamData.getFrustratedCount());

        return target;
    }

    @GraphComputingMetric(name = "/persistence/get/" + ApplicationMetricTable.TABLE)
    @Override public final ApplicationMetric get(String id) {
        return super.get(id);
    }
}
