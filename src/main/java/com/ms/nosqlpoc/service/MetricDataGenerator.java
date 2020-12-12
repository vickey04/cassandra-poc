package com.ms.nosqlpoc.service;

import com.ms.nosqlpoc.domain.MetricData;

import java.util.List;

public interface MetricDataGenerator {

    void generateSnapshotsToFiles(int startTicker, int endTicker, int startRevision, int endRevision, String targetDirectory);

    List<MetricData> generateOneSnapshot(int ticker, int startRevision);

    List<MetricData> generateSnapshots(int startTicker, int endTicker, int startRevision, int endRevision);
}
