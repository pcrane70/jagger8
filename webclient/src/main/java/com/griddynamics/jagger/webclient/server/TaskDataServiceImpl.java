package com.griddynamics.jagger.webclient.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DurationMetricStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.Percentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.webclient.client.StatisticsSummary;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.TaskDetails;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class TaskDataServiceImpl /*extends RemoteServiceServlet*/ implements TaskDataService {
    private static final Logger log = LoggerFactory.getLogger(TaskDataServiceImpl.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSession(String sessionId) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;
        try {
            @SuppressWarnings("unchecked")
            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId=:sessionId and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId=:sessionId) order by td.number asc")
                    .setParameter("sessionId", sessionId).getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (TaskData taskData : taskDataList) {
                taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getTaskName()));
            }

            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during tasks fetching for session " + sessionId, e);
            throw new RuntimeException(e);
        }
        return taskDataDtoList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;

        try {
            List<String> workloadTaskIdList = (List<String>) entityManager.createQuery
                    ("select wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();

            List<Object[]> commonsTasks = (List<Object[]>) entityManager.createQuery
                    ("select td.taskName, td.taskId from TaskData as td where  td.sessionId in (:sessionIds)" +
                            "and td.taskId in (:workloadTaskIdList) group by td.taskId, td.taskName having count(td.id) >= :count")
                    .setParameter("sessionIds", sessionIds)
                    .setParameter("workloadTaskIdList", workloadTaskIdList)
                    .setParameter("count", (long) sessionIds.size())
                    .getResultList();

            Map<String, String> commonsTaskMap = new HashMap<String, String>();
            for (Object[] obj : commonsTasks) {
                String taskName = (String) obj[0];
                String taskId = (String) obj[1];
                commonsTaskMap.put(taskId, taskName);
            }
            log.debug("For sessions {} commons tasks are: {}", sessionIds, commonsTaskMap);

            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId in (:sessionIds) and td.taskId in (:workloadTaskIdList) order by td.number asc")
                    .setParameter("sessionIds", sessionIds)
                    .setParameter("workloadTaskIdList", workloadTaskIdList)
                    .getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            Map<String, TaskDataDto> added = new LinkedHashMap<String, TaskDataDto>();
            for (TaskData taskData : taskDataList) {
                String taskName = taskData.getTaskName();
                String taskId = taskData.getTaskId();
                Long id = taskData.getId();

                if (!commonsTaskMap.containsKey(taskId) || !commonsTaskMap.get(taskId).equals(taskName)) {
                    continue;
                }

                if (!added.containsKey(taskId)) {
                    added.put(taskId, new TaskDataDto(id, taskName));
                }
                added.get(taskId).getIds().add(id);
            }
            taskDataDtoList = new ArrayList<TaskDataDto>(added.values());

            log.info("For sessions {} were loaded {} tasks for {} ms", new Object[]{sessionIds, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during common tasks fetching for sessions " + sessionIds, e);
            throw new RuntimeException(e);
        }

        return taskDataDtoList;
    }

    @Override
    public Map<String, TaskDetails> getTaskDetails(Set<Long> taskIds) {
        Map<String, WorkloadTaskData> taskDataPerSession = loadWorkloadTaskData(taskIds);
        Map<String, WorkloadProcessDescriptiveStatistics> statsPerSession = loadWorkloadProcessStats(taskIds);
        Multimap<String, DurationMetricStatistics> durationStatsPerSession = loadDurationMetricStatistics(taskIds);


        Map<String, TaskDetails> result = Maps.newHashMap();
        for (Map.Entry<String, WorkloadTaskData> entry : taskDataPerSession.entrySet()) {
            String sessionId = entry.getKey();

            TaskDetails details = new TaskDetails();
            WorkloadTaskData workloadTaskData = taskDataPerSession.get(sessionId);
            StatisticsSummary latency = new StatisticsSummary();
            latency.setName("latency");
            latency.setAvg(workloadTaskData.getAvgLatency());
            latency.setStdDev(workloadTaskData.getStdDevLatency());
            Map<Double, Double> percentiles = Maps.newHashMap();
            if (statsPerSession.containsKey(sessionId)) {
                for (Percentile p : statsPerSession.get(sessionId).getPercentiles()) {
                    percentiles.put(p.getPercentileKey(), p.getPercentileValue());
                }
            }

            latency.setPercentiles(percentiles);
            details.setLatency(latency);
            details.setThroughput(workloadTaskData.getThroughput());
            details.setSuccessRate(workloadTaskData.getSuccessRate());
            List<StatisticsSummary> additionalMetrics = Lists.newArrayList();
            for (DurationMetricStatistics stat : durationStatsPerSession.get(sessionId)) {
                StatisticsSummary theMetric = new StatisticsSummary();
                theMetric.setName(stat.getName());
                theMetric.setAvg(BigDecimal.ZERO); // TODO fix
                theMetric.setStdDev(BigDecimal.ZERO); // TODO fix
                Map<Double, Double> sp = Maps.newHashMap();
                for (Percentile p : stat.getPercentiles()) {
                    sp.put(p.getPercentileKey(), p.getPercentileValue());
                }
                theMetric.setPercentiles(sp);
                additionalMetrics.add(theMetric);
            }
            details.setAdditionalMetrics(additionalMetrics);

            result.put(sessionId, details);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Multimap<String, DurationMetricStatistics> loadDurationMetricStatistics(Set<Long> taskIds) {
        List<DurationMetricStatistics> stats = entityManager.createQuery("select s from DurationMetricStatistics s where s.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        Multimap<String, DurationMetricStatistics> statsPerSession = HashMultimap.create();

        for (DurationMetricStatistics stat : stats) {
            statsPerSession.put(stat.getTaskData().getSessionId(), stat);
        }
        return statsPerSession;

    }

    @SuppressWarnings("unchecked")
    private Map<String, WorkloadProcessDescriptiveStatistics> loadWorkloadProcessStats(Set<Long> taskIds) {
        List<WorkloadProcessDescriptiveStatistics> stats = entityManager.createQuery("select s from WorkloadProcessDescriptiveStatistics s where s.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        Map<String, WorkloadProcessDescriptiveStatistics> statsPerSession = Maps.newHashMap();

        for (WorkloadProcessDescriptiveStatistics stat : stats) {
            statsPerSession.put(stat.getTaskData().getSessionId(), stat);
        }
        return statsPerSession;
    }

    @SuppressWarnings("unchecked")
    private Map<String, WorkloadTaskData> loadWorkloadTaskData(Set<Long> taskIds) {
        List<WorkloadTaskData> data = entityManager.createQuery("select d from WorkloadTaskData d, TaskData td where d.taskId=td.taskId and d.sessionId = td.sessionId and td.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();

        Map<String, WorkloadTaskData> taskDataPerSession = Maps.newHashMap();

        for (WorkloadTaskData taskData : data) {
            taskDataPerSession.put(taskData.getSessionId(), taskData);
        }
        return taskDataPerSession;
    }

}
