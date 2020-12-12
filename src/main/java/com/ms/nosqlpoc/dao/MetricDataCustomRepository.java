package com.ms.nosqlpoc.dao;

import com.ms.nosqlpoc.domain.MetricData;

public interface MetricDataCustomRepository {
    MetricData getMetricsDataForTicker(String revision, String ticker, String metric);
}
