package com.ms.nosqlpoc.web;

import com.ms.nosqlpoc.domain.MetricData;
import com.ms.nosqlpoc.domain.MetricsTimeseries;
import com.ms.nosqlpoc.service.CassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CassandraController {

    @Autowired
    private CassandraService cassandraService;

    @GetMapping("/get/{revision}/{ticker}/{metric}")
    public List<MetricData> getMetricsDataForTicker(@PathVariable("revision") String revision,
                                                    @PathVariable("ticker") String ticker,
                                                    @PathVariable("metric") String metric) {
        System.out.println("Fetching data for ticker " + ticker + " and metric " + metric);
        return cassandraService.getMetricsDataForTicker(revision, ticker, metric);
    }

    @GetMapping("/get/{revision}/{ticker}")
    public List<MetricData> getMetricsDataForTicker(@PathVariable("revision") String revision,
                                                    @PathVariable("ticker") String ticker) {
        System.out.println("Getting data for ticker " + ticker);
        return cassandraService.getAllMetricsDataForTicker(revision, ticker);
    }

    @PostMapping("/add")
    public String addMetricsDataForTicker(@RequestBody MetricsTimeseries metricsTimeseries) {
        System.out.println("Writing data for ticker " + metricsTimeseries.getTicker());
        if (metricsTimeseries == null)
            throw new RuntimeException("No metrics data received in request");
        List<MetricData> metricDataList = metricsTimeseries.normalizeMetricsTimeseries();
        cassandraService.addMetricsDataForTicker(metricDataList);
        return "Success";
    }

    @GetMapping("/add/bulk")
    public String loadSnapshots(@RequestParam("noOfTickers") int noOfTickers,
                                @RequestParam("startRevision") int startRevision,
                                @RequestParam("endRevision") int endRevision,
                                @RequestParam("toFile") String toFile) {

        boolean generateToFile = Optional.ofNullable(toFile).orElse("true").equalsIgnoreCase("true");
        if (generateToFile)
            cassandraService.generateAndMetricsDataToFiles(noOfTickers, startRevision, endRevision);
        else
            cassandraService.generateAndLoadMetricsData(noOfTickers, startRevision, endRevision);
        System.out.println("Snapshots load completed!");
        return "Success";
    }
}
