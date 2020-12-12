package com.ms.nosqlpoc.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricsTimeseries {

    private String revision;

    private String ticker;

    private Map<String, Map<LocalDate, Double>> metricTimeseries;


    public MetricsTimeseries(String revision, String ticker) {
        this.revision = revision;
        this.ticker = ticker;
    }

    public void setMetricTimeseries(Map<String, Map<LocalDate, Double>> metricTimeseries) {
        this.metricTimeseries = metricTimeseries;
    }

    public String getRevision() {
        return revision;
    }

    public String getTicker() {
        return ticker;
    }

    public Map<String, Map<LocalDate, Double>> getMetricTimeseries() {
        return metricTimeseries;
    }

    public List<MetricData> normalizeMetricsTimeseries() {
        List<MetricData> metricDataList = new ArrayList<>();
        for (Map.Entry<String, Map<LocalDate, Double>> metricsTimeseriesMap : metricTimeseries.entrySet()) {

            String metric = metricsTimeseriesMap.getKey();
            Map<LocalDate, Double> metricValuesMap = metricsTimeseriesMap.getValue();

            if (metricValuesMap == null || metricValuesMap.isEmpty()) {
                System.out.println("No data for metric - " + metric);
            }

            for (Map.Entry<LocalDate, Double> metricValueEntry : metricValuesMap.entrySet()) {
                MetricKey metricKey = new MetricKey(revision, ticker, metric, metricValueEntry.getKey());
                Double metricValue = metricValueEntry.getValue();
                MetricData metricData = new MetricData(metricKey, metricValue);
                metricDataList.add(metricData);
            }
        }
        return metricDataList;
    }
}
