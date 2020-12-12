package com.ms.nosqlpoc.domain;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class MetricKey2 implements Serializable {

    @PrimaryKeyColumn(name = "revision", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    String revision;

    @PrimaryKeyColumn(name = "ticker", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    String ticker;

    public MetricKey2() {
    }

    public MetricKey2(String revision, String ticker) {
        this.revision = revision;
        this.ticker = ticker;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricKey2 that = (MetricKey2) o;
        return revision.equals(that.revision) &&
                ticker.equals(that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, ticker);
    }
}
