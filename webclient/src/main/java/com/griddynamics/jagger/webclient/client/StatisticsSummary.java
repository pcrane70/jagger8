package com.griddynamics.jagger.webclient.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class StatisticsSummary implements Serializable {
    private String name;
    private BigDecimal avg;
    private BigDecimal stdDev;
    private Map<Double, Double> percentiles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public BigDecimal getStdDev() {
        return stdDev;
    }

    public void setStdDev(BigDecimal stdDev) {
        this.stdDev = stdDev;
    }

    public Map<Double, Double> getPercentiles() {
        return percentiles;
    }

    public void setPercentiles(Map<Double, Double> percentiles) {
        this.percentiles = percentiles;
    }

    @Override
    public String toString() {
        return "StatisticsSummary{" +
                "name='" + name + '\'' +
                ", avg=" + avg +
                ", stdDev=" + stdDev +
                ", percentiles=" + percentiles +
                '}';
    }
}
