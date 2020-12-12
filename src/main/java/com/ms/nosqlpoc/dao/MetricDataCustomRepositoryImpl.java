package com.ms.nosqlpoc.dao;

import com.ms.nosqlpoc.domain.MetricData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

@Repository
public class MetricDataCustomRepositoryImpl implements MetricDataCustomRepository {

    @Autowired
    private CassandraOperations cassandraTemplate;


    @Override
    public MetricData getMetricsDataForTicker(String revision, String ticker, String metric) {
        return null;
    }
}
