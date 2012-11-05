package com.griddynamics.jagger.webclient.client.trends;

import com.google.gwt.i18n.client.NumberFormat;
import com.griddynamics.jagger.webclient.client.StatisticsSummary;
import com.griddynamics.jagger.webclient.client.TaskDetails;

import java.math.BigDecimal;
import java.util.*;

public class TaskDetailView {
    private final Collection<String> sessions;
    private final List<Metric> metrics;

    public static Builder builder() {
        return new Builder();
    }

    public TaskDetailView(Collection<String> sessions, List<Metric> metrics) {
        this.sessions = sessions;
        this.metrics = metrics;
    }

    public Collection<String> getSessions() {
        return sessions;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public static class Metric {
        private String name;
        private Map<String, String> valuesPerSession;

        public Metric(String name, Map<String, String> valuesPerSession) {
            this.name = name;
            this.valuesPerSession = valuesPerSession;
        }

        public String getName() {
            return name;
        }

        public String getValue(String session) {
            return valuesPerSession.get(session);
        }

        public void setValuesPerSession(Map<String, String> valuesPerSession) {
            this.valuesPerSession = valuesPerSession;
        }
    }


    public static class Builder {
        private SortedSet<String> sessions = new TreeSet<String>();
        private LinkedHashSet<String> metrics = new LinkedHashSet<String>();
        private Map<String, Map<String, String>> taskData = new HashMap<String, Map<String, String>>();

        public TaskDetailView build() {
            return new TaskDetailView(sessions, flatten());
        }

        private List<Metric> flatten() {
            List<Metric> result = new ArrayList<Metric>();

            for (String metricName : metrics) {
                result.add(new Metric(metricName, taskData.get(metricName)));
            }

            return result;
        }

        public Builder processDetails(String session, TaskDetails details) {
            sessions.add(session);

            addMetric(session, "Success Rate", toStr(details.getSuccessRate()));
            addMetric(session, "Throughput", toStr(details.getThroughput()));

            StatisticsSummary latency = details.getLatency();
            addSummary(session, latency);

            for (StatisticsSummary additionalMetric : details.getAdditionalMetrics()) {
                addSummary(session, additionalMetric);
            }

            return this;
        }

        private void addSummary(String session, StatisticsSummary latency) {
            addMetric(session, latency.getName() + " avg", toStr(latency.getAvg()));
            addMetric(session, latency.getName() + " stddev", toStr(latency.getStdDev()));

            List<Double> percentiles = new ArrayList<Double>(latency.getPercentiles().keySet());
            Collections.sort(percentiles);

            for (Double percentile : percentiles) {
                Double val = latency.getPercentiles().get(percentile);
                addMetric(session, latency.getName() + " percentile " + percentile, toStr(val));
            }
        }

        private String toStr(double val) {
            return NumberFormat.getFormat("#.000").format(val);
        }

        private String toStr(BigDecimal val) {
            return NumberFormat.getFormat("#.000").format(val);
        }

        private void addMetric(String session, String param, String val) {
            metrics.add(param);
            Map<String, String> map = taskData.get(param);
            if (map == null) {
                map = new HashMap<String, String>();
                taskData.put(param, map);
            }
            map.put(session, val);
        }
    }
}
