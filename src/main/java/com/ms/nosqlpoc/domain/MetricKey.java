package com.ms.nosqlpoc.domain;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@PrimaryKeyClass
public class MetricKey implements Serializable {

    @PrimaryKeyColumn(name = "revision", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    String revision;

    @PrimaryKeyColumn(name = "ticker", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    String ticker;

    @PrimaryKeyColumn(name = "metric", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    String metric;

    @PrimaryKeyColumn(name = "period", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    LocalDate period;

    public MetricKey() {
    }

    public MetricKey(String revision, String ticker, String metric, LocalDate period) {
        this.revision = revision;
        this.ticker = ticker;
        this.metric = metric;
        this.period = period;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricKey metricKey = (MetricKey) o;
        return Objects.equals(revision, metricKey.revision) &&
                Objects.equals(ticker, metricKey.ticker) &&
                Objects.equals(metric, metricKey.metric) &&
                Objects.equals(period, metricKey.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, ticker, metric, period);
    }
}
