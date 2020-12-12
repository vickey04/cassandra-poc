package com.ms.nosqlpoc.service;

import com.ms.nosqlpoc.domain.MetricData;
import com.ms.nosqlpoc.domain.MetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class MetricDataGeneratorImpl implements MetricDataGenerator {

    private final static int NO_OF_METRICS = 1;
    private final static LocalDate START_DATE = LocalDate.of(2004, 12, 31);
    private final static LocalDate END_DATE = LocalDate.of(2019, 12, 1);
    private final static String METRIC_PREFIX = "MTR";
    private final static String TICKER_PREFIX = "TICK";
    private final static String REVISION_PREFIX = "REV";

    private final static long METRIC_VALUE_SEED = 100;
    private final static double METRIC_MIN_VALUE = 100.0;
    private final static double METRIC_MAX_VALUE = 10000.0;
    private final static String OUTPUT_FILE_NAME_PATTERN = "{0}{1}{2}_metric_data.csv";
    private final static String OUTPUT_FILE_PART_SUFFIX = "_part_";

    private final static int WRITE_BUFFER_SIZE = 8192;
    private final static long OUTPUT_FILE_LINE_LIMIT = 5_000_000;

    private final static NumberFormat METRIC_VALUE_FORMATTER = new DecimalFormat("#0.000");


    @Autowired
    private Environment env;

    @Override
    public void generateSnapshotsToFiles(int startTicker, int endTicker, int startRevision, int endRevision, String targetDirectory) {
        Random random = new Random();
        random.setSeed(METRIC_VALUE_SEED);

        List<String> revisions = getPrefixedStrings(REVISION_PREFIX, startRevision, endRevision);
        List<String> tickers = getPrefixedStrings(TICKER_PREFIX, startTicker, endTicker);
        List<String> metrics = getPrefixedStrings(METRIC_PREFIX, NO_OF_METRICS);
        List<String> dates = getDatesStrBetween(START_DATE, END_DATE, "Q");

        System.out.println("Generating records for: ");
        for (String revision : revisions) {
            long lineCount = 0;
            long filePartCount = 0;
            List<String> records = new ArrayList<>();
            for (String ticker : tickers) {
                System.out.println("Revision : " + revision + " Ticker: " + ticker);
                for (String metric : metrics) {
                    for (String date : dates) {
                        String metricValue = METRIC_VALUE_FORMATTER.format((METRIC_MAX_VALUE - METRIC_MIN_VALUE) * random.nextDouble());
                        records.add(String.join(",", revision, ticker, metric, date, metricValue));
                        lineCount++;
                    }
                }
                if (lineCount >= OUTPUT_FILE_LINE_LIMIT) {
                    String filePath = targetDirectory + MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, OUTPUT_FILE_PART_SUFFIX, ++filePartCount);
                    writeListToFile(records, WRITE_BUFFER_SIZE, filePath);
                    records.clear();
                    lineCount = 0;
                }
            }
            String fileName = filePartCount > 0 ? MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, OUTPUT_FILE_PART_SUFFIX, ++filePartCount)
                    : MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, "", "");
            writeListToFile(records, WRITE_BUFFER_SIZE, targetDirectory + fileName);
        }
    }

    @Override
    public List<MetricData> generateOneSnapshot(int ticker, int startRevision) {
        return generateSnapshots(ticker, ticker, startRevision, startRevision);
    }

    @Override
    public List<MetricData> generateSnapshots(int startTicker, int endTicker, int startRevision, int endRevision) {
        List<MetricData> metricDataList = new ArrayList<>();
        Random random = new Random();
        random.setSeed(METRIC_VALUE_SEED);

        System.out.println("Generating records for tickers between " + startTicker + " to " + endTicker + " and revisions " + startRevision + " to " + endRevision);
        List<String> revisions = getPrefixedStrings(REVISION_PREFIX, startRevision, endRevision);
        List<String> tickers = getPrefixedStrings(TICKER_PREFIX, startTicker, endTicker);
        List<String> metrics = getPrefixedStrings(METRIC_PREFIX, NO_OF_METRICS);
        List<LocalDate> dates = getDatesBetween(START_DATE, END_DATE, "Q");

        for (String revision : revisions) {
            for (String ticker : tickers) {
                for (String metric : metrics) {
                    for (LocalDate date : dates) {
                        Double metricValue = (METRIC_MAX_VALUE - METRIC_MIN_VALUE) * random.nextDouble();
                        MetricKey metricKey = new MetricKey(revision, ticker, metric, date);
                        MetricData metricData = new MetricData(metricKey, metricValue);
                        metricDataList.add(metricData);
                    }
                }
            }

        }
        return metricDataList;
    }

    private static File deleteAndCreateOutputFile(String outputFilePath) {
        File file = new File(outputFilePath);
        try {
            if (file.exists()) {
                boolean isDeleted = file.delete();
                System.out.println("File deletion status: " + isDeleted);
            }
            boolean isCreated = file.createNewFile();
            System.out.println("File creation status: " + isCreated);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating output file...!");
        }
        return file;
    }

    private static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate, String frequency) {
        List<LocalDate> dates = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = startDate; date.isBefore(endDate); date = incrementDateByFrequency(date, frequency)) {
            dates.add(date);
        }
        return dates;
    }

    private static List<String> getDatesStrBetween(LocalDate startDate, LocalDate endDate, String frequency) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = startDate; date.isBefore(endDate); date = incrementDateByFrequency(date, frequency)) {
            dates.add(dateTimeFormatter.format(date));
        }
        return dates;
    }

    private static LocalDate incrementDateByFrequency(LocalDate startDate, String frequency) {
        LocalDate newDate;
        switch (frequency) {
            case "D":
                newDate = startDate.plusDays(1);
                break;
            case "M":
                newDate = startDate.plusMonths(1);
                break;
            case "Q":
                newDate = startDate.plusMonths(3);
                break;
            case "Y":
                newDate = startDate.plusYears(1);
                break;
            default:
                throw new IllegalArgumentException("Frequency not valid!");
        }
        return newDate;
    }

    private List<Double> getRandomNumberList2(int totalNumbers, double min, double max) {
        Random random = new Random();
        return random.doubles(totalNumbers, min, max).boxed().collect(Collectors.toList());
    }

    private List<Double> getRandomNumberList(int totalNumbers, double min, double max) {
        Random random = new Random();
        List<Double> numList = new ArrayList<>();
        for (int i = 0; i < totalNumbers; i++) {
            numList.add(min + (max - min) * random.nextDouble());
        }
        return numList;
    }

    private List<String> getPrefixedStrings(String prefix, int size) {
        return getPrefixedStrings(prefix, 1, size);
    }

    private List<String> getPrefixedStrings(String prefix, int startRevision, int endRevision) {
        List<String> prefixedStrings = new ArrayList<>();
        for (int i = startRevision; i <= endRevision; i++) {
            prefixedStrings.add(prefix + i);
        }
        return prefixedStrings;
    }


    private void writeListToFile(List<String> records, int bufSize, String filePath) {
        File file = deleteAndCreateOutputFile(filePath);
        try (FileWriter writer = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize)) {
            for (String record : records) {
                bufferedWriter.write(record);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing file...!");
        }
    }
}
