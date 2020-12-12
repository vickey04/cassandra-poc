package com.ms.nosqlpoc.service;

import com.ms.nosqlpoc.dao.MetricDataRepository;
import com.ms.nosqlpoc.domain.MetricData;
import com.ms.nosqlpoc.domain.MetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class CassandraServiceImpl implements CassandraService {

    @Autowired
    private MetricDataRepository metricDataRepository;

    @Autowired
    private MetricDataGenerator metricDataGenerator;

    private static final String DELIMITER = ",";

    private static final int SNAPSHOT_CHUNK_SIZE = 100;

    final static String OUTPUT_FILE_PATH = "D:\\financial-metrics-data\\";

    @Override
    public List<MetricData> getMetricsDataForTicker(String revision, String ticker, String metric) {
        List<MetricData> metricsDataForTicker = metricDataRepository.getMetricsDataForTicker(revision, ticker, metric);
        return metricsDataForTicker;
    }

    @Override
    public List<MetricData> getAllMetricsDataForTicker(String revision, String ticker) {
        List<MetricData> metricsDataForTicker = metricDataRepository.getAllMetricsDataForTicker(revision, ticker);
        return metricsDataForTicker;
    }

    @Override
    public void addMetricsDataForTicker(List<MetricData> metricDataList) {
        System.out.println("Loading to Cassandra.. size " + metricDataList.size());
        metricDataRepository.saveAll(metricDataList);
    }

    @Override
    public void generateAndLoadMetricsData(int noOfTickers, int startRevision, int endRevision) {
        for (int i = startRevision; i <= endRevision; i++) {
            System.out.println("Processing snapshots in chunks of size " + SNAPSHOT_CHUNK_SIZE);
            int endOfChunkTicker = 0;
            if (noOfTickers >= SNAPSHOT_CHUNK_SIZE) {
                int noOfChunks = noOfTickers / SNAPSHOT_CHUNK_SIZE;
                for (int tickerOffset = 0; tickerOffset < noOfChunks; tickerOffset++) {
                    int startTicker = tickerOffset * SNAPSHOT_CHUNK_SIZE;
                    endOfChunkTicker = startTicker + SNAPSHOT_CHUNK_SIZE;
                    System.out.println("Snapshots chunk no. " + (tickerOffset + 1) + " of " + noOfChunks);
                    List<MetricData> metricDataList = metricDataGenerator.generateSnapshots(startTicker, endOfChunkTicker, startRevision, endRevision);
                    addMetricsDataForTicker(metricDataList);
                }
            }
            if (endOfChunkTicker < noOfTickers) {
                int remainingTickers = noOfTickers - endOfChunkTicker;
                System.out.println("Generating remaining " + remainingTickers + " snapshots");
                List<MetricData> metricDataList = metricDataGenerator.generateSnapshots(endOfChunkTicker, noOfTickers, startRevision, endRevision);
                addMetricsDataForTicker(metricDataList);
            }
        }
    }

    @Override
    public void generateAndMetricsDataToFiles(int noOfTickers, int startRevision, int endRevision) {
/*        for (int i = startRevision; i <= endRevision; i++) {
            System.out.println("Processing snapshots in chunks of size " + SNAPSHOT_CHUNK_SIZE);
            int endOfChunkTicker = 0;
            if (noOfTickers >= SNAPSHOT_CHUNK_SIZE) {
                int noOfChunks = noOfTickers / SNAPSHOT_CHUNK_SIZE;
                for (int tickerOffset = 0; tickerOffset < noOfChunks; tickerOffset++) {
                    int startTicker = tickerOffset * SNAPSHOT_CHUNK_SIZE;
                    endOfChunkTicker = startTicker + SNAPSHOT_CHUNK_SIZE;
                    System.out.println("Snapshots chunk no. " + (tickerOffset + 1) + " of " + noOfChunks);
                    metricDataGenerator.generateSnapshotsToFiles(startTicker, endOfChunkTicker, startRevision, endRevision, OUTPUT_FILE_PATH);
                }
            }
            if (endOfChunkTicker < noOfTickers) {
                int remainingTickers = noOfTickers - endOfChunkTicker;
                System.out.println("Generating remaining " + remainingTickers + " snapshots");
                metricDataGenerator.generateSnapshotsToFiles(endOfChunkTicker, noOfTickers, startRevision, endRevision, OUTPUT_FILE_PATH);
            }
        }*/
        System.out.println("Calling metric data generator...");
        int startTicker = 1;
        int endTicker = startTicker + noOfTickers - 1;
        metricDataGenerator.generateSnapshotsToFiles(startTicker, endTicker, startRevision, endRevision, OUTPUT_FILE_PATH);
    }

    @Override
    public void loadMetricsDataFromDisk(String directory) {
        List<Path> metricDataFiles = findMetricDataFiles(directory);
        loadMetricData(metricDataFiles);
    }

    private void loadMetricData(List<Path> metricDataFiles) {
        for (Path path : metricDataFiles) {
            File file = path.toFile();
            System.out.println("Processing file " + file.getName());
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                List<MetricData> metricsDataList = bufferedReader.lines().map(this::mapToMetricData).collect(toList());
                addMetricsDataForTicker(metricsDataList);
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while processing file - " + file.getName(), e);
            }
        }
    }

    private MetricData mapToMetricData(String line) {
        String[] values = line.split(DELIMITER);
        String revision = values[0];
        String ticker = values[1];
        String metric = values[2];
        LocalDate period = LocalDate.parse(values[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Double metricValue = Double.parseDouble(values[4]);
        MetricKey metricKey = new MetricKey(revision, ticker, metric, period);
        return new MetricData(metricKey, metricValue);
    }


    private List<Path> findMetricDataFiles(String directory) {
        List<Path> dataFiles = null;
        try (Stream<Path> paths = Files.walk(Paths.get(directory), 2)) {
            dataFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().matches("metrics*.csv"))
                    .collect(toList());
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while parsing files");
        }
        if (dataFiles == null)
            throw new RuntimeException("Couldn't find any matching files for data load");
        return dataFiles;
    }
}
