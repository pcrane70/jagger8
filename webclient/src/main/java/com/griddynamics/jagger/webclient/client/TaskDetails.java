package com.griddynamics.jagger.webclient.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class TaskDetails implements Serializable {
    private StatisticsSummary latency;
    private List<StatisticsSummary> additionalMetrics;
    private BigDecimal throughput;
    private BigDecimal successRate;

    public StatisticsSummary getLatency() {
        return latency;
    }

    public void setLatency(StatisticsSummary latency) {
        this.latency = latency;
    }

    public List<StatisticsSummary> getAdditionalMetrics() {
        return additionalMetrics;
    }

    public void setAdditionalMetrics(List<StatisticsSummary> additionalMetrics) {
        this.additionalMetrics = additionalMetrics;
    }

    public BigDecimal getThroughput() {
        return throughput;
    }

    public void setThroughput(BigDecimal throughput) {
        this.throughput = throughput;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    @Override
    public String toString() {
        return "TaskDetails{" +
                "latency=" + latency +
                ", additionalMetrics=" + additionalMetrics +
                ", throughput=" + throughput +
                ", successRate=" + successRate +
                '}';
    }
}
