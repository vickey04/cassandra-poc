package com.ms.nosqlpoc;

import com.ms.nosqlpoc.service.CassandraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@EnableCassandraRepositories
public class MetricDataTest {

    @Autowired
    private CassandraService cassandraService;

    @Test
    public void testSnapshotsGenerator() {
        cassandraService.generateAndLoadMetricsData(500, 1, 1);
    }

    @Configuration
    @ComponentScan("com.ms.nosqlpoc")
    public static class SpringConfig {
    }
}
