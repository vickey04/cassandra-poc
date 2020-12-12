package com.ms.nosqlpoc.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("metric_timeseries")
public class MetricData {

    @PrimaryKey
    private MetricKey metricKey;

    @Column("metric_value")
    private Double metricValue;

    public MetricData() {
    }

    public MetricData(MetricKey metricKey, Double metricValue) {
        this.metricKey = metricKey;
        this.metricValue = metricValue;
    }

    public MetricKey getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(MetricKey metricKey) {
        this.metricKey = metricKey;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
}
