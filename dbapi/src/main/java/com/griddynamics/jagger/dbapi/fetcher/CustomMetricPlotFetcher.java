package com.griddynamics.jagger.dbapi.fetcher;


import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.DataProcessingUtil;

import java.util.*;

public class CustomMetricPlotFetcher extends AbstractMetricPlotFetcher {

    @Override
    protected Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames) {

        Set<String> metricIds = new HashSet<String>();
        Set<Long> taskIds = new HashSet<Long>();

        for (MetricNameDto metricName : metricNames) {
            metricIds.add(metricName.getMetricName());
            taskIds.addAll(metricName.getTaskIds());
        }

        List<Object[]> rawData = getRawData(taskIds, metricIds);

        if (rawData.isEmpty()) {
            log.warn("No plot data found for metrics {} within taskData ids {}", metricIds, taskIds);
            return Collections.emptyList();
        }

        List<MetricRawData> resultList = new ArrayList<MetricRawData>();
        for (Object[] objects : rawData) {
            MetricRawData metricRawData = new MetricRawData();

            metricRawData.setWorkloadTaskDataId(((Number)objects[0]).longValue());
            metricRawData.setTime((Long)objects[1]);
            metricRawData.setValue((Double)objects[2]);
            metricRawData.setSessionId((String)objects[3]);
            metricRawData.setMetricId((String)objects[4]);

            resultList.add(metricRawData);
        }

        return resultList;
    }

    /**
     * @return collection of objects {Task Data id, time, value, sessionId, metricId}
     */
    protected List<Object[]> getRawData(Set<Long> taskDataIds, Set<String> metricIds) {

        if (taskDataIds.isEmpty() || metricIds.isEmpty()) {
            log.warn("Empty data for getRawData() method {}; {}" + taskDataIds, metricIds);
            return Collections.emptyList();
        }
        List<Object[]> resultList = new ArrayList<Object[]>();
        resultList.addAll(getPlotDataNewModel(taskDataIds, metricIds));
        resultList.addAll(getPlotDataOldModel(taskDataIds, metricIds));
        return resultList;
    }

    /**
     * @return collection of objects {Task Data id, time, value, sessionId, metricId}
     */
    private Collection<? extends Object[]> getPlotDataOldModel(Set<Long> taskIds, Set<String> metricIds) {
        return entityManager.createQuery(
                "select metrics.taskData.id, metrics.time, metrics.value, metrics.taskData.sessionId, metrics.metric from MetricDetails metrics " +
                        "where metrics.metric in (:metricIds) and metrics.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .setParameter("metricIds", metricIds)
                .getResultList();
    }

    /**
     * @return collection of objects {Task Data id, time, value, sessionId, metricId}
     */
    protected Collection<? extends Object[]> getPlotDataNewModel(Set<Long> taskIds, Set<String> metricIds) {
        try {
            return entityManager.createQuery(
                    "select mpe.metricDescription.taskData.id, mpe.time, mpe.value, mpe.metricDescription.taskData.sessionId, mpe.metricDescription.metricId from MetricPointEntity as mpe " +
                            "where mpe.metricDescription.taskData.id in (:taskIds) and mpe.metricDescription.metricId in (:metricIds)")
                    .setParameter("taskIds", taskIds)
                    .setParameter("metricIds", metricIds)
                    .getResultList();
        } catch (Exception e) {
            log.debug("Could not fetch metric plots from MetricPointEntity: ", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptyList();
        }
    }

}
