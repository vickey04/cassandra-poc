package com.ms.nosqlpoc.domain;

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

public class RandomDataGenerator {

    final static int NO_OF_REVISIONS = 1;
    final static int NO_OF_TICKERS = 1000;
    final static int NO_OF_METRICS = 500;
    final static LocalDate START_DATE = LocalDate.of(1990, 1, 1);
    final static LocalDate END_DATE = LocalDate.of(2020, 6, 1);
    final static String METRIC_PREFIX = "MTR";
    final static String TICKER_PREFIX = "TICK";
    final static String REVISION_PREFIX = "REV";

    final static long METRIC_VALUE_SEED = 100;
    final static double METRIC_MIN_VALUE = 100.0;
    final static double METRIC_MAX_VALUE = 10000.0;
    final static String OUTPUT_FILE_PATH = "D:\\Coding\\";
    final static String OUTPUT_FILE_NAME_PATTERN = "{0}{1}{2}_metric_data.csv";
    final static String OUTPUT_FILE_PART_SUFFIX = "_part_";

    final static int WRITE_BUFFER_SIZE = 8192;
    final static long OUTPUT_FILE_LINE_LIMIT = 5_000_000;

    final static NumberFormat METRIC_VALUE_FORMATTER = new DecimalFormat("#0.000");

    public static void main(String[] args) {
        Random random = new Random();
        random.setSeed(METRIC_VALUE_SEED);

        System.out.println("Generating " + NO_OF_REVISIONS + " revisions of random data for " + NO_OF_TICKERS + " tickers with " + NO_OF_METRICS + " metrics");
        List<String> revisions = getPrefixedStrings(REVISION_PREFIX, NO_OF_REVISIONS);
        List<String> tickers = getPrefixedStrings(TICKER_PREFIX, NO_OF_TICKERS);
        List<String> metrics = getPrefixedStrings(METRIC_PREFIX, NO_OF_METRICS);
        List<String> dates = getDatesBetween(START_DATE, END_DATE, "M");

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
                        records.add(String.join(",", ticker, revision, metric, date, metricValue));
                        lineCount++;
                    }
                }
                if (lineCount >= OUTPUT_FILE_LINE_LIMIT) {
                    String filePath = OUTPUT_FILE_PATH + MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, OUTPUT_FILE_PART_SUFFIX, ++filePartCount);
                    writeListToFile(records, WRITE_BUFFER_SIZE, filePath);
                    records.clear();
                    lineCount = 0;
                }
            }
            String fileName = filePartCount > 0 ? MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, OUTPUT_FILE_PART_SUFFIX, ++filePartCount)
                    : MessageFormat.format(OUTPUT_FILE_NAME_PATTERN, revision, "", "");
            writeListToFile(records, WRITE_BUFFER_SIZE, OUTPUT_FILE_PATH + fileName);
        }
    }

    private static File deleteAndCreateOuputFile(String outputFilePath) {
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

    private static List<String> getDatesBetween(LocalDate startDate, LocalDate endDate, String frequency) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = startDate; date.isBefore(endDate); date = incrementDateByFrequency(date, 1, frequency)) {
            dates.add(dateTimeFormatter.format(date));
        }
        return dates;
    }

    private static LocalDate incrementDateByFrequency(LocalDate startDate, int incrementBy, String frequency) {
        LocalDate newDate;
        switch (frequency) {
            case "D":
                newDate = startDate.plusDays(incrementBy);
                break;
            case "M":
                newDate = startDate.plusMonths(incrementBy);
                break;
            case "Y":
                newDate = startDate.plusYears(incrementBy);
                break;
            default:
                throw new IllegalArgumentException("Frequency not valid!");
        }
        return newDate;
    }

    private static List<Double> getRandomNumberList2(int totalNumbers, double min, double max) {
        Random random = new Random();
        return random.doubles(totalNumbers, min, max).boxed().collect(Collectors.toList());
    }

    private static List<Double> getRandomNumberList(int totalNumbers, double min, double max) {
        Random random = new Random();
        List<Double> numList = new ArrayList<>();
        for (int i = 0; i < totalNumbers; i++) {
            numList.add(min + (max - min) * random.nextDouble());
        }
        return numList;
    }

    private static List<String> getPrefixedStrings(String prefix, int size) {
        List<String> prefixedStrings = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            prefixedStrings.add(prefix + i);
        }
        return prefixedStrings;
    }

    private static void writeListToFile(List<String> records, int bufSize, String filePath) {
        File file = deleteAndCreateOuputFile(filePath);
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