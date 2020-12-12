package com.ms.nosqlpoc.service;

import com.ms.nosqlpoc.domain.MetricData;

import java.util.List;

public interface CassandraService {

    List<MetricData> getMetricsDataForTicker(String revision, String ticker, String metric);

    List<MetricData> getAllMetricsDataForTicker(String revision, String ticker);

    //void addMetricsDataForTicker(MetricsTimeseries metricsTimeseries);

    void addMetricsDataForTicker(List<MetricData> metricDataList);

    void generateAndLoadMetricsData(int noOfTickers, int startRevision, int endRevision);

    void generateAndMetricsDataToFiles(int noOfTickers, int startRevision, int endRevision);

    void loadMetricsDataFromDisk(String directory);
}
