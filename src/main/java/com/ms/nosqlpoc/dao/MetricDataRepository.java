package com.ms.nosqlpoc.dao;

import com.ms.nosqlpoc.domain.MetricData;
import com.ms.nosqlpoc.domain.MetricKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricDataRepository extends CassandraRepository<MetricData, MetricKey> {

    @Query("SELECT * FROM financial_metrics.metric_timeseries where revision = ?0 and ticker = ?1 and metric = ?2")
    List<MetricData> getMetricsDataForTicker(String revision, String ticker, String metric);

    @Query(value = "SELECT * FROM financial_metrics.metric_timeseries where revision = ?0 and ticker = ?1 ALLOW FILTERING")
    List<MetricData> getAllMetricsDataForTicker(String revision, String ticker);

}
